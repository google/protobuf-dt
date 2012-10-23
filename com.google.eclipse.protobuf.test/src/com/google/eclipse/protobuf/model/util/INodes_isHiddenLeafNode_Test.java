/*
 * Copyright (c) 2011 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.model.util;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.eclipse.xtext.nodemodel.ILeafNode;
import org.eclipse.xtext.nodemodel.INode;
import org.junit.Before;
import org.junit.Test;

/**
 * Tests for <code>{@link INodes#isHiddenLeafNode(INode)}</code>
 *
 * @author alruiz@google.com (Alex Ruiz)
 */
public class INodes_isHiddenLeafNode_Test {
  private INodes nodes;

  @Before public void setUp() {
    nodes = new INodes();
  }

  @Test public void should_return_true_if_given_node_is_an_ILeafNode_and_is_hidden() {
    ILeafNode node = mock(ILeafNode.class);
    when(node.isHidden()).thenReturn(true);
    assertTrue(nodes.isHiddenLeafNode(node));
  }

  @Test public void should_return_false_if_given_node_is_an_ILeafNode_but_is_not_hidden() {
    ILeafNode node = mock(ILeafNode.class);
    when(node.isHidden()).thenReturn(false);
    assertFalse(nodes.isHiddenLeafNode(node));
  }

  @Test public void should_return_false_if_given_node_is_not_an_ILeafNode() {
    INode node = mock(INode.class);
    assertFalse(nodes.isHiddenLeafNode(node));
  }
}
