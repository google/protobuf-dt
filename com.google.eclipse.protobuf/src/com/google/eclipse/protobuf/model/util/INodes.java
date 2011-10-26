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

import org.eclipse.emf.ecore.*;
import org.eclipse.xtext.*;
import org.eclipse.xtext.nodemodel.*;

import com.google.inject.Singleton;

/**
 * Utility methods related to <code>{@link INode}</code>s.
 *
 * @author alruiz@google.com (Alex Ruiz)
 */
@Singleton
public class INodes {

  /**
   * Returns the first node that was used to assign values to the given feature for the given object.
   * @param o the given object.
   * @param feature the given feature.
   * @return the first node that was used to assign values to the given feature for the given object, or {@code null} if
   * a node cannot be found.
   */
  public INode firstNodeForFeature(EObject o, EStructuralFeature feature) {
    List<INode> nodes = findNodesForFeature(o, feature);
    if (nodes.isEmpty()) return null;
    return nodes.get(0);
  }

  /**
   * Indicates whether the given node belongs to a string, or a single- or multi-line comment.
   * @param node the node to check.
   * @return {@code true} if the given node belongs to a string, or a single- or multi-line comment; {@code false}
   * otherwise.
   */
  public boolean belongsToCommentOrString(INode node) {
    return belongsToComment(node) || belongsToString(node);
  }

  /**
   * Indicates whether the given node belongs to a single- or multiple-line comment.
   * @param node the node to check.
   * @return {@code true} if the given node belongs to a single- or multiple-line comment; {@code false} otherwise.
   */
  public boolean belongsToComment(INode node) {
    return belongsToSingleLineComment(node) || belongsToMultipleLineComment(node);
  }

  /**
   * Indicates whether the given node belongs to a single-line comment.
   * @param node the node to check.
   * @return {@code true} if the given node belongs to a single-line comment; {@code false} otherwise.
   */
  public boolean belongsToSingleLineComment(INode node) {
    return belongsToComment(node, "SL_COMMENT");
  }

  /**
   * Indicates whether the given node belongs to a multiple-line comment.
   * @param node the node to check.
   * @return {@code true} if the given node belongs to a multiple-line comment; {@code false} otherwise.
   */
  public boolean belongsToMultipleLineComment(INode node) {
    return belongsToComment(node, "ML_COMMENT");
  }

  private static boolean belongsToComment(INode node, String commentRuleName) {
    if (!(node instanceof ILeafNode)) return false;
    EObject o = node.getGrammarElement();
    if (!(o instanceof TerminalRule)) return false;
    TerminalRule rule = (TerminalRule) o;
    return commentRuleName.equals(rule.getName());
  }

  /**
   * Indicates whether the given node belongs to a string.
   * @param node the node to check.
   * @return {@code true} if the given node belongs to a string; {@code false} otherwise.
   */
  public boolean belongsToString(INode node) {
    EObject grammarElement = node.getGrammarElement();
    if (!(grammarElement instanceof RuleCall)) return false;
    AbstractRule rule = ((RuleCall) grammarElement).getRule();
    if (!(rule instanceof TerminalRule)) return false;
    TerminalRule terminalRule = (TerminalRule) rule;
    return "STRING".equals(terminalRule.getName());
  }
  
  /**
   * Indicates whether the given node is a hidden leaf node.
   * @param node the node to check.
   * @return {@code true} if the given node is a hidden leaf node; {@code false} otherwise.
   */
  public boolean isHiddenLeafNode(INode node) {
    if (!(node instanceof ILeafNode)) return false;
    return ((ILeafNode) node).isHidden();
  }
}
