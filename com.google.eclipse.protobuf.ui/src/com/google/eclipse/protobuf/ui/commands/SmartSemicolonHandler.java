/*
 * Copyright (c) 2011 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.ui.commands;

import static com.google.eclipse.protobuf.grammar.CommonKeyword.SEMICOLON;
import static com.google.eclipse.protobuf.protobuf.ProtobufPackage.Literals.*;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.xtext.nodemodel.INode;
import org.eclipse.xtext.resource.XtextResource;
import org.eclipse.xtext.ui.editor.XtextEditor;
import org.eclipse.xtext.ui.editor.contentassist.ContentAssistContext;
import org.eclipse.xtext.ui.editor.contentassist.antlr.ParserBasedContentAssistContextFactory;
import org.eclipse.xtext.util.concurrent.IUnitOfWork;

import com.google.eclipse.protobuf.protobuf.*;
import com.google.eclipse.protobuf.ui.util.*;
import com.google.eclipse.protobuf.util.ModelNodes;
import com.google.inject.Inject;

/**
 * Inserts a semicolon at the end of a line, regardless of the current position of the caret in the editor. If the
 * line of code being edited is a property or enum literal and if it does not have an index yet, this handler will
 * insert an index with a proper value as well.
 *
 * @author alruiz@google.com (Alex Ruiz)
 */
public class SmartSemicolonHandler extends SmartInsertHandler {

  @Inject private ParserBasedContentAssistContextFactory contextFactory;
  @Inject private Fields fields;
  @Inject private Literals literals;
  @Inject private ModelNodes nodes;

  private final String semicolon = SEMICOLON.toString();

  /** {@inheritDoc} */
  @Override protected void insertContent(XtextEditor editor, StyledText styledText) {
    int originalCaretOffset = styledText.getCaretOffset();
    int lineAtOffset = styledText.getLineAtOffset(originalCaretOffset);
    int offsetAtLine = styledText.getOffsetAtLine(lineAtOffset);
    String line = styledText.getLine(lineAtOffset);
    if (line.endsWith(semicolon)) {
      behaveLikeRegularEditing(styledText, originalCaretOffset);
      return;
    }
    int endOfLineOffset = offsetAtLine + line.length();
    styledText.setCaretOffset(endOfLineOffset);
    String content = contentToInsert(editor, originalCaretOffset);
    insert(styledText, content, endOfLineOffset);
  }

  private void behaveLikeRegularEditing(StyledText styledText, int caretOffset) {
    insert(styledText, semicolon, caretOffset);
  }

  private void insert(StyledText styledText, String content, int caretOffset) {
    styledText.insert(content);
    styledText.setCaretOffset(caretOffset + content.length());
  }

  private String contentToInsert(final XtextEditor editor, final int offset) {
    return editor.getDocument().modify(new IUnitOfWork<String, XtextResource>() {
      public String exec(XtextResource state) {
        ContentAssistContext[] context = contextFactory.create(editor.getInternalSourceViewer(), offset, state);
        if (context == null || context.length == 0) return semicolon;
        for (ContentAssistContext c : context) {
          EObject model = c.getCurrentModel();
          if (model instanceof Literal)
            return contentToInsert((Literal) model);
          if (model instanceof Property)
            return contentToInsert((Property) model);
        }
        return semicolon;
      }
    });
  }

  private String contentToInsert(Literal literal) {
    INode indexNode = nodes.firstNodeForFeature(literal, LITERAL__INDEX);
    if (indexNode != null) return semicolon;
    int index = literals.calculateIndexOf(literal);
    return defaultIndexAndSemicolonToInsert(index);
  }

  private String contentToInsert(Property property) {
    INode indexNode = nodes.firstNodeForFeature(property, FIELD__INDEX);
    if (indexNode != null) return semicolon;
    int index = fields.calculateTagNumberOf(property);
    return defaultIndexAndSemicolonToInsert(index);
  }

  private String defaultIndexAndSemicolonToInsert(int index) {
    return indexAndSemicolonToInsert("= %d%s", index);
  }

  private String indexAndSemicolonToInsert(String format, int index) {
    return String.format(format, index, semicolon);
  }
}
