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
import org.eclipse.xtext.nodemodel.INode;

import com.google.inject.Singleton;

/**
 * Utility methods related to <code>{@link INode}</code>s.
 *
 * @author alruiz@google.com (Alex Ruiz)
 */
@Singleton
public class INodes {

  private static final String SINGLE_LINE_COMMENT_RULE_NAME = "SL_COMMENT";

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
   * Indicates whether the given node was created by a string, or a single- or multi-line comment.
   * @param node the node to check.
   * @return {@code true} if the given node was created by a string, or a single- or multi-line comment; {@code false}
   * otherwise.
   */
  public boolean wasCreatedByAnyCommentOrString(INode node) {
    return wasCreatedByAnyComment(node) || wasCreatedByString(node);
  }

  /**
   * Indicates whether the given node was created by a single- or multi-line comment.
   * @param node the node to check.
   * @return {@code true} if the given node was created by a single- or multi-line comment; {@code false} otherwise.
   */
  public boolean wasCreatedByAnyComment(INode node) {
    return wasCreatedByRule(node, SINGLE_LINE_COMMENT_RULE_NAME, "ML_COMMENT");
  }

  /**
   * Indicates whether the given node was created by a string.
   * @param node the node to check.
   * @return {@code true} if the given node was created by a string; {@code false} otherwise.
   */
  public boolean wasCreatedByString(INode node) {
    EObject grammarElement = node.getGrammarElement();
    if (!(grammarElement instanceof RuleCall)) return false;
    AbstractRule rule = ((RuleCall) grammarElement).getRule();
    if (!(rule instanceof TerminalRule)) return false;
    TerminalRule terminalRule = (TerminalRule) rule;
    return "STRING".equals(terminalRule.getName());
  }

  /**
   * Indicates whether the given node was created by a single-line comment.
   * @param node the node to check.
   * @return {@code true} if the given node was created by a single-line comment; {@code false} otherwise.
   */
  public boolean wasCreatedBySingleLineComment(INode node) {
    return wasCreatedByRule(node, SINGLE_LINE_COMMENT_RULE_NAME);
  }

  private boolean wasCreatedByRule(INode node, String...ruleNames) {
    EObject o = node.getGrammarElement();
    if (!(o instanceof TerminalRule)) return false;
    TerminalRule rule = (TerminalRule) o;
    String actualName = rule.getName();
    for (String name : ruleNames) {
      if (name.equalsIgnoreCase(actualName)) return true;
    }
    return false;
  }
}
