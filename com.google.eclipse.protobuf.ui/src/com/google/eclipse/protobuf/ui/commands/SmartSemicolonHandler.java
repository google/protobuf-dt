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
import static org.eclipse.xtext.util.Strings.isEmpty;

import com.google.eclipse.protobuf.grammar.CommonKeyword;
import com.google.eclipse.protobuf.model.util.INodes;
import com.google.eclipse.protobuf.protobuf.*;
import com.google.eclipse.protobuf.ui.preferences.pages.editor.numerictag.*;
import com.google.eclipse.protobuf.ui.util.*;
import com.google.inject.Inject;

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
import org.eclipse.xtext.util.Pair;
import org.eclipse.xtext.util.concurrent.IUnitOfWork;

import java.util.regex.*;

/**
 * Inserts a semicolon at the end of a line, regardless of the current position of the caret in the editor. If the
 * line of code being edited is a property or enum literal and if it does not have an index yet, this handler will
 * insert an index with a proper value as well.
 *
 * @author alruiz@google.com (Alex Ruiz)
 */
public class SmartSemicolonHandler extends SmartInsertHandler {

  private static final Pattern NUMBERS_PATTERN = Pattern.compile("[\\d]+");

  private static Logger logger = Logger.getLogger(SmartSemicolonHandler.class);

  @Inject private CommentNodesFinder commentNodesFinder;
  @Inject private Fields fields;
  @Inject private Literals literals;
  @Inject private INodes nodes;
  @Inject private NumericTagPreferencesFactory preferencesFactory;
  @Inject private ParserBasedContentAssistContextFactory contextFactory;

  private static final String SEMICOLON = CommonKeyword.SEMICOLON.toString();

  private static final ContentToInsert INSERT_SEMICOLON_AT_CURRENT_LOCATION = new ContentToInsert(SEMICOLON, Location.CURRENT);

  /** {@inheritDoc} */
  @Override protected void insertContent(XtextEditor editor, StyledText styledText) {
    int offset = styledText.getCaretOffset();
    int lineAtOffset = styledText.getLineAtOffset(offset);
    int offsetAtLine = styledText.getOffsetAtLine(lineAtOffset);
    String line = styledText.getLine(lineAtOffset);
    ContentToInsert newContent = ContentToInsert.RETRY;
    int retryCount = 2;
    for (int i = 0; i < retryCount; i++) {
      if (!newContent.equals(ContentToInsert.RETRY)) break;
      newContent = newContent(editor, styledText, line);
      if (newContent.equals(ContentToInsert.NONE)) return;
      if (newContent.equals(ContentToInsert.INSERT_TAG_NUMBER)) {
        refreshHighlighting(editor);
        return;
      }
    }
    if (newContent.location.equals(Location.END)) {
      offset = offsetAtLine + line.length();
      styledText.setCaretOffset(offset);
    }
    styledText.insert(newContent.value);
    styledText.setCaretOffset(offset + newContent.value.length());
  }

  private ContentToInsert newContent(final XtextEditor editor, final StyledText styledText, final String line) {
    if (line.endsWith(SEMICOLON)) return INSERT_SEMICOLON_AT_CURRENT_LOCATION;
    final IXtextDocument document = editor.getDocument();
    try {
      return document.modify(new IUnitOfWork<ContentToInsert, XtextResource>() {
        @Override public ContentToInsert exec(XtextResource resource) {
          int offset = styledText.getCaretOffset();
          ContentAssistContext[] context = contextFactory.create(editor.getInternalSourceViewer(), offset, resource);
          for (ContentAssistContext c : context) {
            if (nodes.belongsToCommentOrString(c.getCurrentNode())) continue;
            EObject model = c.getCurrentModel();
            if (model instanceof FieldOption) {
              FieldOption option = (FieldOption) model;
              model = option.eContainer();
            }
            if (model instanceof Literal) {
              Literal literal = (Literal) model;
              ContentToInsert content = newContent(literal);
              if (content.equals(ContentToInsert.INSERT_TAG_NUMBER)) {
                long index = literals.calculateIndexOf(literal);
                literal.setIndex(index);
                updateIndexInCommentOfParent(literal, index, document);
              }
              return content;
            }
            if (model instanceof Property) {
              Property property = (Property) model;
              ContentToInsert content = newContent(property);
              if (content.equals(ContentToInsert.INSERT_TAG_NUMBER)) {
                long index = fields.calculateTagNumberOf(property);
                property.setIndex(index);
                updateIndexInCommentOfParent(property, index, document);
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

  private ContentToInsert newContent(Literal literal) {
    INode indexNode = nodes.firstNodeForFeature(literal, LITERAL__INDEX);
    return newContent(indexNode);
  }

  private ContentToInsert newContent(Property property) {
    INode indexNode = nodes.firstNodeForFeature(property, FIELD__INDEX);
    return newContent(indexNode);
  }

  private ContentToInsert newContent(INode indexNode) {
    boolean hasIndex = indexNode != null && !isEmpty(indexNode.getText());
    return hasIndex ? new ContentToInsert(SEMICOLON, Location.END) : ContentToInsert.INSERT_TAG_NUMBER;
  }

  private void updateIndexInCommentOfParent(EObject o, long index, IXtextDocument document) {
    EObject parent = o.eContainer();
    if (parent == null) return;
    NumericTagPreferences preferences = preferencesFactory.preferences();
    for (String pattern : preferences.patterns()) {
      Pair<INode, Matcher> match = commentNodesFinder.matchingCommentNode(parent, pattern);
      if (match == null) return;
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

    static final ContentToInsert NONE = new ContentToInsert();
    static final ContentToInsert INSERT_TAG_NUMBER = new ContentToInsert();
    static final ContentToInsert RETRY = new ContentToInsert();

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
