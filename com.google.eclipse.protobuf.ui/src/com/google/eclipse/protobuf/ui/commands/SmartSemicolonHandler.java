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

import com.google.eclipse.protobuf.protobuf.*;
import com.google.eclipse.protobuf.ui.util.*;
import com.google.eclipse.protobuf.util.*;
import com.google.inject.Inject;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.xtext.*;
import org.eclipse.xtext.nodemodel.INode;
import org.eclipse.xtext.resource.XtextResource;
import org.eclipse.xtext.ui.editor.XtextEditor;
import org.eclipse.xtext.ui.editor.contentassist.ContentAssistContext;
import org.eclipse.xtext.ui.editor.contentassist.antlr.ParserBasedContentAssistContextFactory;
import org.eclipse.xtext.util.concurrent.IUnitOfWork;
import org.eclipse.xtext.validation.IConcreteSyntaxValidator.InvalidConcreteSyntaxException;

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
    int offset = styledText.getCaretOffset();
    int lineAtOffset = styledText.getLineAtOffset(offset);
    int offsetAtLine = styledText.getOffsetAtLine(lineAtOffset);
    String line = styledText.getLine(lineAtOffset);
    ContentToInsert newContent = newContent(editor, styledText, line);
    if (newContent.equals(ContentToInsert.NONE)) return;
    if (newContent.location.equals(Location.END)) {
      offset = offsetAtLine + line.length();
      styledText.setCaretOffset(offset);
    }
    styledText.insert(newContent.value);
    styledText.setCaretOffset(offset + newContent.value.length());
  }
  
  private ContentToInsert newContent(final XtextEditor editor, final StyledText styledText, final String line) {
    try {
      return editor.getDocument().modify(new IUnitOfWork<ContentToInsert, XtextResource>() {
        public ContentToInsert exec(XtextResource state) {
          int offset = styledText.getCaretOffset();
          ContentAssistContext[] context = contextFactory.create(editor.getInternalSourceViewer(), offset, state);
          for (ContentAssistContext c : context) {
            INode currentNode = c.getCurrentNode();
            if (nodes.wasCreatedByAnyComment(currentNode) || wasCreatedByString(currentNode)) break;
            EObject model = c.getCurrentModel();
            if (model instanceof FieldOption) {
              FieldOption option = (FieldOption) model;
              model = option.eContainer();
            }
            if (line.endsWith(semicolon)) break;
            if (model instanceof Literal) {
              Literal literal = (Literal) model;
              ContentToInsert content = newContent(literal);
              if (content.equals(ContentToInsert.NONE)) {
                int index = literals.calculateIndexOf(literal);
                literal.setIndex(index);
              }
              return content;
            }
            if (model instanceof Property) {
              Property property = (Property) model;
              ContentToInsert content = newContent(property);
              if (content.equals(ContentToInsert.NONE)) {
                int index = fields.calculateTagNumberOf(property);
                property.setIndex(index);
              }
              return content;
            }
          }
          return new ContentToInsert(semicolon, Location.CURRENT);
        }
      });
    } catch (InvalidConcreteSyntaxException e) {}
    return ContentToInsert.NONE;
  }

  private boolean wasCreatedByString(INode node) {
    EObject grammarElement = node.getGrammarElement();
    if (!(grammarElement instanceof RuleCall)) return false;
    AbstractRule rule = ((RuleCall) grammarElement).getRule();
    if (!(rule instanceof TerminalRule)) return false;
    TerminalRule terminalRule = (TerminalRule) rule;
    return "STRING".equals(terminalRule.getName());
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
    return (indexNode != null) ? new ContentToInsert(semicolon, Location.END) : ContentToInsert.NONE;
  }
  
  private static class ContentToInsert {
    final String value;
    final Location location;

    static final ContentToInsert NONE = new ContentToInsert("", Location.NONE);
    
    ContentToInsert(String value, Location location) {
      this.value = value;
      this.location = location;
    }
  }
  
  private static enum Location {
    NONE, CURRENT, END;
  }
}
