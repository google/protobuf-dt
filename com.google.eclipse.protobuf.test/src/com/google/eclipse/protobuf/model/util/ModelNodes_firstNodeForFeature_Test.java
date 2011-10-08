/*
 * Copyright (c) 2011 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.model.util;

import static com.google.eclipse.protobuf.junit.core.Setups.unitTestSetup;
import static com.google.eclipse.protobuf.junit.core.XtextRule.createWith;
import static com.google.eclipse.protobuf.protobuf.ProtobufPackage.Literals.FIELD__NAME;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;

import com.google.eclipse.protobuf.junit.core.XtextRule;
import com.google.eclipse.protobuf.protobuf.Property;

import org.eclipse.emf.ecore.*;
import org.eclipse.xtext.nodemodel.INode;
import org.junit.*;

/**
 * Tests for <code>{@link INodes#firstNodeForFeature(EObject, EStructuralFeature)}</code>
 *
 * @author alruiz@google.com (Alex Ruiz)
 */
public class ModelNodes_firstNodeForFeature_Test {

  @Rule public XtextRule xtext = createWith(unitTestSetup());

  private INodes nodes;

  @Before public void setUp() {
    nodes = xtext.getInstanceOf(INodes.class);
  }

  // message Person {
  //   optional bool active = 1;    
  // }
  @Test public void should_return_first_node_for_feature() {
    Property active = xtext.find("active", Property.class);
    INode node = nodes.firstNodeForFeature(active, FIELD__NAME);
    assertThat(node.getText().trim(), equalTo("active"));
  }
}
