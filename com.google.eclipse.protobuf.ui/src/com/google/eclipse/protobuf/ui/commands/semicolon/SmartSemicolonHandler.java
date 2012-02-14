/*
 * Copyright (c) 2011 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.ui.commands.semicolon;

import static com.google.eclipse.protobuf.protobuf.ProtobufPackage.Literals.*;
import static java.util.regex.Pattern.compile;
import static org.eclipse.xtext.util.Strings.isEmpty;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.regex.*;

import org.apache.log4j.Logger;
import org.eclipse.emf.ecore.*;
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
import com.google.eclipse.protobuf.ui.commands.SmartInsertHandler;
import com.google.eclipse.protobuf.ui.preferences.editor.numerictag.core.NumericTagPreferences;
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

  private static final IUnitOfWork.Void<XtextResource> NULL_UNIT_OF_WORK = new IUnitOfWork.Void<XtextResource>() {
    @Override public void process(XtextResource resource) {}
  };

  private static Logger logger = Logger.getLogger(SmartSemicolonHandler.class);

  @Inject private CommentNodesFinder commentNodesFinder;
  @Inject private ParserBasedContentAssistContextFactory contextFactory;
  @Inject private IndexedElements indexedElements;
  @Inject private Literals literals;
  @Inject private INodes nodes;
  @Inject private Protobufs protobufs;
  @Inject private Resources resources;
  @Inject private IPreferenceStoreAccess storeAccess;

  private static final String SEMICOLON = CommonKeyword.SEMICOLON.toString();

  @Override protected void insertContent(XtextEditor editor, StyledText styledText) {
    StyledTextAccess styledTextAccess = new StyledTextAccess(styledText);
    String line = styledTextAccess.lineAtCaretOffset();
    if (line.endsWith(SEMICOLON)) {
      styledTextAccess.insert(SEMICOLON);
      return;
    }
    insertContent(editor, styledTextAccess);
    refreshHighlighting(editor);
  }

  private void insertContent(final XtextEditor editor, final StyledTextAccess styledTextAccess) {
    final AtomicBoolean shouldInsertSemicolon = new AtomicBoolean(true);
    final IXtextDocument document = editor.getDocument();
    document.readOnly(NULL_UNIT_OF_WORK); // wait for reconciler to finish its work.
    try {
      document.modify(new IUnitOfWork.Void<XtextResource>() {
        @Override public void process(XtextResource resource) {
          Protobuf root = resources.rootOf(resource);
          if (!protobufs.isProto2(root)) {
            return;
          }
          int offset = styledTextAccess.caretOffset();
          ContentAssistContext[] context = contextFactory.create(editor.getInternalSourceViewer(), offset, resource);
          for (ContentAssistContext c : context) {
            if (nodes.isCommentOrString(c.getCurrentNode())) {
              continue;
            }
            EObject model = modelFrom(c);
            if (model instanceof FieldOption) {
              FieldOption option = (FieldOption) model;
              model = option.eContainer();
            }
            if (model instanceof Literal) {
              Literal literal = (Literal) model;
              if (shouldCalculateIndex(literal, LITERAL__INDEX)) {
                long index = literals.calculateNewIndexOf(literal);
                literal.setIndex(index);
                updateIndexInCommentOfParent(literal, index, document);
                shouldInsertSemicolon.set(false);
              }
            }
            if (model instanceof MessageField) {
              MessageField field = (MessageField) model;
              if (shouldCalculateIndex(field, MESSAGE_FIELD__INDEX)) {
                long index = indexedElements.calculateNewIndexFor(field);
                field.setIndex(index);
                updateIndexInCommentOfParent(field, index, document);
                shouldInsertSemicolon.set(false);
              }
            }
          }
        }
      });
    } catch (Throwable t) {
      logger.error("Unable to generate tag number", t);
    }
    if (shouldInsertSemicolon.get()) {
      styledTextAccess.insert(SEMICOLON);
    }
  }

  private boolean shouldCalculateIndex(EObject target, EAttribute indexAttribute) {
    INode node = nodes.firstNodeForFeature(target, indexAttribute);
    return node == null || isEmpty(node.getText());
  }

  private EObject modelFrom(ContentAssistContext c) {
    EObject current = c.getCurrentModel();
    boolean isIndexed = current instanceof MessageField || current instanceof Literal;
    return (isIndexed) ? current : c.getPreviousModel();
  }

  private void updateIndexInCommentOfParent(EObject target, long index, IXtextDocument document) {
    EObject parent = target.eContainer();
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
}
