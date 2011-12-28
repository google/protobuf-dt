/*
 * Copyright (c) 2011 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.ui.commands;

import static com.google.eclipse.protobuf.protobuf.ProtobufPackage.Literals.*;
import static java.util.regex.Pattern.compile;
import static org.eclipse.xtext.util.Strings.isEmpty;

import java.util.regex.*;

import org.apache.log4j.Logger;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.xtext.nodemodel.INode;
import org.eclipse.xtext.resource.XtextResource;
import org.eclipse.xtext.ui.editor.XtextEditor;
import org.eclipse.xtext.ui.editor.contentassist.ContentAssistContext;
import org.eclipse.xtext.ui.editor.contentassist.antlr.ParserBasedContentAssistContextFactory;
import org.eclipse.xtext.ui.editor.model.IXtextDocument;
import org.eclipse.xtext.ui.editor.preferences.IPreferenceStoreAccess;
import org.eclipse.xtext.util.Pair;
import org.eclipse.xtext.util.concurrent.IUnitOfWork;

import com.google.eclipse.protobuf.grammar.CommonKeyword;
import com.google.eclipse.protobuf.model.util.*;
import com.google.eclipse.protobuf.protobuf.*;
import com.google.eclipse.protobuf.ui.preferences.editor.numerictag.core.NumericTagPreferences;
import com.google.eclipse.protobuf.ui.util.Literals;
import com.google.inject.Inject;

/**
 * Inserts a semicolon at the end of a line, regardless of the current position of the caret in the editor. If the
 * line of code being edited is a field or enum literal and if it does not have an index yet, this handler will
 * insert an index with a proper value as well.
 *
 * @author alruiz@google.com (Alex Ruiz)
 */
public class SmartSemicolonHandler extends SmartInsertHandler {

  private static final Pattern NUMBERS_PATTERN = compile("[\\d]+");

  private static Logger logger = Logger.getLogger(SmartSemicolonHandler.class);

  @Inject private CommentNodesFinder commentNodesFinder;
  @Inject private IndexedElements indexedElements;
  @Inject private Literals literals;
  @Inject private INodes nodes;
  @Inject private ParserBasedContentAssistContextFactory contextFactory;
  @Inject private IPreferenceStoreAccess storeAccess;

  private static final String SEMICOLON = CommonKeyword.SEMICOLON.toString();

  private static final ContentToInsert INSERT_SEMICOLON_AT_CURRENT_LOCATION = new ContentToInsert(SEMICOLON, Location.CURRENT);

  /** {@inheritDoc} */
  @Override protected void insertContent(XtextEditor editor, StyledText styledText) {
    int offset = styledText.getCaretOffset();
    int lineAtOffset = styledText.getLineAtOffset(offset);
    int offsetAtLine = styledText.getOffsetAtLine(lineAtOffset);
    String line = styledText.getLine(lineAtOffset);
    ContentToInsert newContent = newContent(editor, styledText, line);
    if (newContent.equals(ContentToInsert.TAG_NUMBER_INSERTED)) {
      refreshHighlighting(editor);
      return;
    }
    if (newContent.location.equals(Location.END)) {
      offset = offsetAtLine + line.length();
      styledText.setCaretOffset(offset);
    }
    styledText.insert(newContent.value);
    styledText.setCaretOffset(offset + newContent.value.length());
  }

  private ContentToInsert newContent(final XtextEditor editor, final StyledText styledText, final String line) {
    if (line.endsWith(SEMICOLON)) {
      return INSERT_SEMICOLON_AT_CURRENT_LOCATION;
    }
    final IXtextDocument document = editor.getDocument();
    try {
      return document.modify(new IUnitOfWork<ContentToInsert, XtextResource>() {
        @Override public ContentToInsert exec(XtextResource resource) {
          int offset = styledText.getCaretOffset();
          ContentAssistContext[] context = contextFactory.create(editor.getInternalSourceViewer(), offset, resource);
          for (ContentAssistContext c : context) {
            if (nodes.belongsToCommentOrString(c.getCurrentNode())) {
              continue;
            }
            EObject model = modelFrom(c);
            if (model instanceof FieldOption) {
              FieldOption option = (FieldOption) model;
              model = option.eContainer();
            }
            if (model instanceof Literal) {
              Literal literal = (Literal) model;
              ContentToInsert content = newContent(literal);
              if (content.equals(ContentToInsert.TAG_NUMBER_INSERTED)) {
                long index = literals.calculateIndexOf(literal);
                literal.setIndex(index);
                updateIndexInCommentOfParent(literal, index, document);
              }
              return content;
            }
            if (model instanceof MessageField) {
              MessageField field = (MessageField) model;
              ContentToInsert content = newContent(field);
              if (content.equals(ContentToInsert.TAG_NUMBER_INSERTED)) {
                long index = indexedElements.calculateNewIndexFor(field);
                field.setIndex(index);
                updateIndexInCommentOfParent(field, index, document);
              }
              return content;
            }
          }
          return INSERT_SEMICOLON_AT_CURRENT_LOCATION;
        }
      });
    } catch (Throwable e) {
      logger.error("Unable to generate tag number", e);
      return INSERT_SEMICOLON_AT_CURRENT_LOCATION;
    }
  }

  private EObject modelFrom(ContentAssistContext c) {
    EObject current = c.getCurrentModel();
    if (isIndexed(current)) {
      return current;
    }
    return c.getPreviousModel();
  }

  private boolean isIndexed(EObject e) {
    return e instanceof MessageField || e instanceof Literal;
  }

  private ContentToInsert newContent(Literal literal) {
    INode indexNode = nodes.firstNodeForFeature(literal, LITERAL__INDEX);
    ContentToInsert content = newContent(indexNode);
    if (content.equals(ContentToInsert.TAG_NUMBER_INSERTED)) {
      literal.setIndex(-1); // reset to make at semicolon work when new index is zero (TODO fix bug.)
    }
    return content;
  }

  private ContentToInsert newContent(MessageField field) {
    INode indexNode = nodes.firstNodeForFeature(field, MESSAGE_FIELD__INDEX);
    return newContent(indexNode);
  }

  private ContentToInsert newContent(INode indexNode) {
    boolean hasIndex = indexNode != null && !isEmpty(indexNode.getText());
    return hasIndex ? new ContentToInsert(SEMICOLON, Location.END) : ContentToInsert.TAG_NUMBER_INSERTED;
  }

  private void updateIndexInCommentOfParent(EObject o, long index, IXtextDocument document) {
    EObject parent = o.eContainer();
    if (parent == null) {
      return;
    }
    NumericTagPreferences preferences = new NumericTagPreferences(storeAccess);
    for (String pattern : preferences.patterns().getValue()) {
      Pair<INode, Matcher> match = commentNodesFinder.matchingCommentNode(parent, pattern);
      if (match == null) {
        return;
      }
      String original = match.getSecond().group();
      String replacement = NUMBERS_PATTERN.matcher(original).replaceFirst(String.valueOf(index + 1));
      INode node = match.getFirst();
      int offset = node.getTotalOffset() + node.getText().indexOf(original);
      try {
        document.replace(offset, original.length(), replacement);
      } catch (BadLocationException e) {
        String format = "Unable to update comment tracking next tag number using pattern '%s'";
        logger.error(String.format(format, pattern), e);
      }
    }
  }

  private void refreshHighlighting(final XtextEditor editor) {
    editor.getDocument().readOnly(new IUnitOfWork.Void<XtextResource>() {
      @Override public void process(XtextResource resource) {
        editor.getInternalSourceViewer().invalidateTextPresentation();
      }
    });
  }

  private static class ContentToInsert {
    final String value;
    final Location location;

    static final ContentToInsert TAG_NUMBER_INSERTED = new ContentToInsert();

    ContentToInsert() {
      this("", Location.NONE);
    }

    ContentToInsert(String value, Location location) {
      this.value = value;
      this.location = location;
    }

    /** {@inheritDoc} */
    @Override public String toString() {
      return String.format("ContentToInsert [value=%s, location=%s]", value, location);
    }
  }

  private static enum Location {
    NONE, CURRENT, END;
  }
}
