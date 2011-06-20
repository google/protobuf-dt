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
  @Override protected void insertContent(XtextEditor editor) {
    StyledText styledText = styledTextFrom(editor);
    int originalCaretOffset = styledText.getCaretOffset();
    int lineAtOffset = styledText.getLineAtOffset(originalCaretOffset);
    int offsetAtLine = styledText.getOffsetAtLine(lineAtOffset);
    String line = styledText.getLine(lineAtOffset);
    if (line.endsWith(semicolon)) {
      behaveLikeRegularEditing(originalCaretOffset, styledText);
      return;
    }
    int endOfLineOffset = offsetAtLine + line.length();
    styledText.setCaretOffset(endOfLineOffset);
    String contentToInsert = contentToInsert(line, editor, originalCaretOffset);
    styledText.insert(contentToInsert);
    styledText.setCaretOffset(endOfLineOffset + contentToInsert.length());
  }

  private void behaveLikeRegularEditing(int caretOffset, StyledText styledText) {
    styledText.insert(semicolon);
    styledText.setCaretOffset(caretOffset + semicolon.length());
  }

  private String contentToInsert(final String line, final XtextEditor editor, final int offset) {
    return editor.getDocument().readOnly(new IUnitOfWork<String, XtextResource>() {
      public String exec(XtextResource state) {
        ContentAssistContext[] context = contextFactory.create(editor.getInternalSourceViewer(), offset, state);
        if (context == null || context.length == 0) return semicolon;
        for (ContentAssistContext c : context) {
          EObject model = c.getCurrentModel();
          if (model instanceof Literal)
            return contentToInsert(line, (Literal) model);
          if (model instanceof Property)
            return contentToInsert(line, (Property) model);
        }
        return semicolon;
      }
    });
  }

  private String contentToInsert(String line, Literal literal) {
    INode indexNode = nodes.firstNodeForFeature(literal, LITERAL__INDEX);
    if (indexNode != null) return semicolon;
    int index = literals.calculateIndexOf(literal);
    return defaultIndexAndSemicolonToInsert(line, index);
  }

  private String contentToInsert(String line, Property property) {
    INode indexNode = nodes.firstNodeForFeature(property, FIELD__INDEX);
    if (indexNode != null) return semicolon;
    int index = fields.calculateTagNumberOf(property);
    return defaultIndexAndSemicolonToInsert(line, index);
  }

  private String defaultIndexAndSemicolonToInsert(String line, int index) {
    return indexAndSemicolonToInsert("= %d%s", line, index);
  }

  private String indexAndSemicolonToInsert(String format, String line, int index) {
    return String.format(format, index, semicolon);
  }
}
