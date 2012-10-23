/*
 * Copyright (c) 2011 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.model.util;

import static org.eclipse.xtext.nodemodel.util.NodeModelUtils.findNodesForFeature;

import java.util.List;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.xtext.AbstractRule;
import org.eclipse.xtext.RuleCall;
import org.eclipse.xtext.TerminalRule;
import org.eclipse.xtext.nodemodel.ILeafNode;
import org.eclipse.xtext.nodemodel.INode;

import com.google.inject.Singleton;

/**
 * Utility methods related to <code>{@link INode}</code>s.
 *
 * @author alruiz@google.com (Alex Ruiz)
 */
@Singleton public class INodes {
  /**
   * Returns the first node that was used to assign values to the given feature for the given object.
   * @param o the given object.
   * @param feature the given feature.
   * @return the first node that was used to assign values to the given feature for the given object, or {@code null} if
   * a node cannot be found.
   */
  public INode firstNodeForFeature(EObject o, EStructuralFeature feature) {
    List<INode> nodes = findNodesForFeature(o, feature);
    if (nodes.isEmpty()) {
      return null;
    }
    return nodes.get(0);
  }

  /**
   * Indicates whether the given node belongs to a string, or a single- or multi-line comment.
   * @param node the node to check.
   * @return {@code true} if the given node belongs to a string, or a single- or multi-line comment; {@code false}
   * otherwise.
   */
  public boolean isCommentOrString(INode node) {
    return isComment(node) || isString(node);
  }

  /**
   * Indicates whether the given node belongs to a single- or multiple-line comment.
   * @param node the node to check.
   * @return {@code true} if the given node belongs to a single- or multiple-line comment; {@code false} otherwise.
   */
  public boolean isComment(INode node) {
    return isSingleLineComment(node) || isMultipleLineComment(node);
  }

  /**
   * Indicates whether the given node belongs to a single-line comment.
   * @param node the node to check.
   * @return {@code true} if the given node belongs to a single-line comment; {@code false} otherwise.
   */
  public boolean isSingleLineComment(INode node) {
    return isComment(node, "SL_COMMENT");
  }

  /**
   * Indicates whether the given node belongs to a multiple-line comment.
   * @param node the node to check.
   * @return {@code true} if the given node belongs to a multiple-line comment; {@code false} otherwise.
   */
  public boolean isMultipleLineComment(INode node) {
    return isComment(node, "ML_COMMENT");
  }

  private static boolean isComment(INode node, String commentRuleName) {
    if (!(node instanceof ILeafNode)) {
      return false;
    }
    EObject o = node.getGrammarElement();
    if (!(o instanceof TerminalRule)) {
      return false;
    }
    TerminalRule rule = (TerminalRule) o;
    return commentRuleName.equals(rule.getName());
  }

  /**
   * Indicates whether the given node belongs to a string.
   * @param node the node to check.
   * @return {@code true} if the given node belongs to a string; {@code false} otherwise.
   */
  public boolean isString(INode node) {
    EObject grammarElement = node.getGrammarElement();
    if (!(grammarElement instanceof RuleCall)) {
      return false;
    }
    AbstractRule rule = ((RuleCall) grammarElement).getRule();
    if (!(rule instanceof TerminalRule)) {
      return false;
    }
    TerminalRule terminalRule = (TerminalRule) rule;
    return "STRING".equals(terminalRule.getName());
  }

  /**
   * Indicates whether the given node is a hidden leaf node.
   * @param node the node to check.
   * @return {@code true} if the given node is a hidden leaf node; {@code false} otherwise.
   */
  public boolean isHiddenLeafNode(INode node) {
    if (node instanceof ILeafNode) {
      return ((ILeafNode) node).isHidden();
    }
    return false;
  }

  /**
   * Returns the text of the given node, with leading and trailing whitespace omitted.
   * @param node the given node, may be {@code null}.
   * @return the text of the given node, with leading and trailing whitespace omitted.
   */
  public String textOf(INode node) {
    if (node == null) {
      return null;
    }
    String text = node.getText();
    return (text == null) ? null : text.trim();
  }
}
