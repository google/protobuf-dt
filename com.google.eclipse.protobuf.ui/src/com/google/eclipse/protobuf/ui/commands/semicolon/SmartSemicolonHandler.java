/*
 * Copyright (c) 2015 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.ui.commands.semicolon;

import static com.google.eclipse.protobuf.protobuf.ProtobufPackage.Literals.LITERAL__INDEX;
import static java.util.regex.Pattern.compile;
import static org.eclipse.xtext.util.Strings.isEmpty;

import com.google.common.annotations.VisibleForTesting;
import com.google.eclipse.protobuf.model.util.INodes;
import com.google.eclipse.protobuf.model.util.IndexedElements;
import com.google.eclipse.protobuf.model.util.Literals;
import com.google.eclipse.protobuf.model.util.Protobufs;
import com.google.eclipse.protobuf.model.util.Resources;
import com.google.eclipse.protobuf.protobuf.Enum;
import com.google.eclipse.protobuf.protobuf.FieldOption;
import com.google.eclipse.protobuf.protobuf.Group;
import com.google.eclipse.protobuf.protobuf.IndexedElement;
import com.google.eclipse.protobuf.protobuf.Literal;
import com.google.eclipse.protobuf.protobuf.Message;
import com.google.eclipse.protobuf.protobuf.MessageElement;
import com.google.eclipse.protobuf.protobuf.MessageField;
import com.google.eclipse.protobuf.ui.commands.SmartInsertHandler;
import com.google.eclipse.protobuf.ui.preferences.editor.numerictag.NumericTagPreferences;
import com.google.inject.Inject;

import org.apache.log4j.Logger;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.Region;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.custom.StyledTextContent;
import org.eclipse.text.edits.DeleteEdit;
import org.eclipse.text.edits.MultiTextEdit;
import org.eclipse.text.edits.ReplaceEdit;
import org.eclipse.text.edits.TextEdit;
import org.eclipse.xtext.EcoreUtil2;
import org.eclipse.xtext.RuleCall;
import org.eclipse.xtext.nodemodel.ILeafNode;
import org.eclipse.xtext.nodemodel.INode;
import org.eclipse.xtext.nodemodel.util.NodeModelUtils;
import org.eclipse.xtext.resource.XtextResource;
import org.eclipse.xtext.ui.editor.XtextEditor;
import org.eclipse.xtext.ui.editor.contentassist.ContentAssistContext;
import org.eclipse.xtext.ui.editor.contentassist.antlr.ParserBasedContentAssistContextFactory;
import org.eclipse.xtext.ui.editor.model.IXtextDocument;
import org.eclipse.xtext.ui.editor.preferences.IPreferenceStoreAccess;
import org.eclipse.xtext.util.concurrent.IUnitOfWork;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Handles a semicolon keypress either by completing the element at the cursor position with a tag
 * number if that element is a message field, group, or enum literal lacking a tag number, or
 * otherwise by inserting a semicolon at the cursor position.
 */
public class SmartSemicolonHandler extends SmartInsertHandler {
  private static final String SEMICOLON = ";";
  private static final Pattern NUMBERS_PATTERN = compile("[\\d]+");

  private static Logger logger = Logger.getLogger(SmartSemicolonHandler.class);

  @Inject private ParserBasedContentAssistContextFactory contextFactory;
  @Inject private IndexedElements indexedElements;
  @Inject private Literals literals;
  @Inject private INodes nodes;
  @Inject private Protobufs protobufs;
  @Inject private Resources resources;
  @Inject private IPreferenceStoreAccess storeAccess;

  @Override protected void insertContent(final XtextEditor editor, final StyledText styledText) {
    final IXtextDocument document = editor.getDocument();

    document.modify(new IUnitOfWork.Void<XtextResource>() {
      @Override public void process(XtextResource resource) {
        if (!protobufs.hasKnownSyntax(resources.rootOf(resource))) {
          return;
        }

        EObject completableElement =
            findCompletableElement(editor, styledText.getCaretOffset(), resource);

        long newIndex = determineNewIndex(completableElement);
        if (newIndex != -1) {
          final TextEdit edit = new MultiTextEdit();

          TextEdit indexEdit =
              completeWithIndex(NodeModelUtils.getNode(completableElement), newIndex);
          if (indexEdit != null) {
            edit.addChild(indexEdit);

            TextEdit trailingWhitespaceEdit =
                deleteTrailingWhitespace(styledText.getContent(), indexEdit.getOffset());
            if (trailingWhitespaceEdit != null) {
              edit.addChild(trailingWhitespaceEdit);
            }

            long newNextIndex = newIndex + 1;
            TextEdit commentEdit = updateNextIndexComment(completableElement, newNextIndex);
            if (commentEdit != null) {
              edit.addChild(commentEdit);
            }

            try {
              edit.apply(document);

              // Move the cursor to the end of the inserted completion text.
              styledText.setCaretOffset(indexEdit.getExclusiveEnd());
            } catch (BadLocationException e) {
              logger.error("Failed to complete element with new tag number", e);
            }
          }
        } else {
          styledText.insert(SEMICOLON);
          styledText.setCaretOffset(styledText.getCaretOffset() + SEMICOLON.length());
        }
      }
    });

    // Refresh syntax highlighting etc.
    editor.getInternalSourceViewer().invalidateTextPresentation();
  }

  private EObject findCompletableElement(XtextEditor editor, int offset, XtextResource resource) {
    ContentAssistContext[] contexts =
        contextFactory.create(editor.getInternalSourceViewer(), offset, resource);

    for (ContentAssistContext context : contexts) {
      if (nodes.isCommentOrString(context.getCurrentNode())) {
        continue;
      }

      for (EObject model : Arrays.asList(context.getCurrentModel(), context.getPreviousModel())) {
        if (model instanceof FieldOption) {
          model = model.eContainer();
        }

        if (model instanceof MessageField || model instanceof Group || model instanceof Literal) {
          return model;
        }
      }
    }

    return null;
  }

  @VisibleForTesting long determineNewIndex(EObject model) {
    if (model instanceof IndexedElement) {
      IndexedElement indexedElement = (IndexedElement) model;
      if (indexedElements.indexOf(indexedElement) <= 0) {
        return indexedElements.calculateNewIndexFor(indexedElement);
      }
    } else if (model instanceof Literal) {
      Literal literal = (Literal) model;
      INode node = nodes.firstNodeForFeature(literal, LITERAL__INDEX);
      if (node == null || isEmpty(node.getText())) {
        return literals.calculateNewIndexOf(literal);
      }
    }

    return -1;
  }

  @VisibleForTesting ReplaceEdit completeWithIndex(INode elementNode, long newIndex) {
    INode nameNode = null;
    INode equalsNode = null;
    INode optionsBracketNode = null;
    INode groupBraceNode = null;
    for (INode leafNode : elementNode.getAsTreeIterable()) {
      if (leafNode.getGrammarElement() instanceof RuleCall
          && ((RuleCall) leafNode.getGrammarElement()).getRule().getName().equals("ID")) {
        nameNode = leafNode;
      } else {
        String text = leafNode.getText();
        if (text.equals("=")) {
          equalsNode = leafNode;
        } else if (text.equals("[")) {
          optionsBracketNode = leafNode;
        } else if (text.equals("{")) {
          groupBraceNode = leafNode;
        }
      }
    }

    if (nameNode == null) {
      return null;
    }

    StringBuilder replacement = new StringBuilder();

    int start;
    if (equalsNode != null) {
      start = equalsNode.getTotalEndOffset();
    } else {
      start = nameNode.getTotalEndOffset();
      replacement.append(" =");
    }

    replacement.append(" ");
    replacement.append(newIndex);

    int end;
    if (optionsBracketNode != null) {
      end = optionsBracketNode.getTotalOffset();
      replacement.append(" ");
    } else if (groupBraceNode != null) {
      end = groupBraceNode.getTotalOffset();
      replacement.append(" ");
    } else {
      end = elementNode.getTotalEndOffset();
      if (elementNode.getGrammarElement() instanceof RuleCall
          && ((RuleCall) elementNode.getGrammarElement()).getRule().getName().equals("Group")) {
        // Insert a space after the index of a new group
        // so that the user can easily continue typing { or [.
        replacement.append(" ");
      } else {
        replacement.append(SEMICOLON);
      }
    }

    return new ReplaceEdit(start, end - start, replacement.toString());
  }

  @VisibleForTesting TextEdit deleteTrailingWhitespace(StyledTextContent content, int offset) {
    int lineAtOffset = content.getLineAtOffset(offset);
    int offsetWithinLine = offset - content.getOffsetAtLine(lineAtOffset);
    String lineText = content.getLine(lineAtOffset);

    String trailingText = lineText.substring(offsetWithinLine);
    int trailingTextLength = trailingText.length();

    if (trailingText.trim().length() == 0) {
      return new DeleteEdit(offset, trailingTextLength);
    }

    return null;
  }

  @VisibleForTesting ReplaceEdit updateNextIndexComment(
      EObject completedElement, long newNextIndex) {
    Class<? extends EObject> containingClass =
        completedElement instanceof IndexedElement ? Message.class : Enum.class;
    EObject containingElement = EcoreUtil2.getContainerOfType(completedElement, containingClass);
    Iterable<ILeafNode> topLevelCommentNodes = findTopLevelCommentNodes(containingElement);

    Collection<Pattern> patterns = compileIndexCommentPatterns();

    IRegion indexLocation = findNextIndexInComments(topLevelCommentNodes, patterns);
    if (indexLocation != null) {
      return new ReplaceEdit(
          indexLocation.getOffset(), indexLocation.getLength(), String.valueOf(newNextIndex));
    }

    return null;
  }

  private Iterable<ILeafNode> findTopLevelCommentNodes(EObject containingElement) {
    Set<ILeafNode> nestedLeafNodes = new HashSet<>();
    if (containingElement instanceof Message) {
      Collection<MessageElement> nestedContainers = new ArrayList<>();
      nestedContainers.addAll(EcoreUtil2.getAllContentsOfType(containingElement, Message.class));
      nestedContainers.addAll(EcoreUtil2.getAllContentsOfType(containingElement, Enum.class));
      for (MessageElement nestedContainer : nestedContainers) {
        for (ILeafNode nestedLeafNode : NodeModelUtils.getNode(nestedContainer).getLeafNodes()) {
          nestedLeafNodes.add(nestedLeafNode);
        }
      }
    }

    Collection<ILeafNode> topLevelCommentNodes = new ArrayList<>();
    for (ILeafNode leafNode : NodeModelUtils.getNode(containingElement).getLeafNodes()) {
      if (!nestedLeafNodes.contains(leafNode) && nodes.isComment(leafNode)) {
        topLevelCommentNodes.add(leafNode);
      }
    }

    return topLevelCommentNodes;
  }

  private Collection<Pattern> compileIndexCommentPatterns() {
    List<String> regexes = new NumericTagPreferences(storeAccess).patterns();
    Collection<Pattern> patterns = new ArrayList<>(regexes.size());
    for (String regex : regexes) {
      patterns.add(Pattern.compile(regex));
    }
    return patterns;
  }

  private IRegion findNextIndexInComments(
      Iterable<ILeafNode> commentNodes, Collection<Pattern> patterns) {
    for (ILeafNode commentNode : commentNodes) {
      for (Pattern pattern : patterns) {
        Matcher patternMatcher = pattern.matcher(commentNode.getText());
        if (patternMatcher.find()) {
          Matcher numberMatcher = NUMBERS_PATTERN.matcher(patternMatcher.group());
          if (numberMatcher.find()) {
            int matchStartPosition =
                commentNode.getTotalOffset() + patternMatcher.start() + numberMatcher.start();
            return new Region(matchStartPosition, numberMatcher.end() - numberMatcher.start());
          }
        }
      }
    }

    return null;
  }
}
