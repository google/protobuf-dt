/*
 * Copyright (c) 2011 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.model.util;

import static org.eclipse.xtext.nodemodel.util.NodeModelUtils.getNode;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import static com.google.eclipse.protobuf.junit.core.UnitTestModule.unitTestModule;
import static com.google.eclipse.protobuf.junit.core.XtextRule.overrideRuntimeModuleWith;

import org.eclipse.xtext.nodemodel.ICompositeNode;
import org.eclipse.xtext.nodemodel.ILeafNode;
import org.eclipse.xtext.nodemodel.INode;
import org.junit.Rule;
import org.junit.Test;

import com.google.eclipse.protobuf.junit.core.XtextRule;
import com.google.inject.Inject;

/**
 * Tests for <code>{@link INodes#isString(INode)}</code>
 *
 * @author alruiz@google.com (Alex Ruiz)
 */
public class INodes_isString_Test {
  @Rule public XtextRule xtext = overrideRuntimeModuleWith(unitTestModule());

  @Inject private INodes nodes;

  // syntax = "proto2";
  //
  // message Person {
  //   optional string name = 1 [default = 'Alex'];
  // }
  @Test public void should_return_true_if_node_belongs_to_string() {
    ILeafNode node = xtext.findNode("'Alex'");
    assertTrue(nodes.isString(node));
  }

  // syntax = "proto2";
  //
  // message Person {}
  @Test public void should_return_false_if_node_does_not_belong_to_string() {
    ICompositeNode node = getNode(xtext.root());
    assertFalse(nodes.isString(node));
  }
}
