/*
 * Copyright (c) 2011 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.util;

import static com.google.eclipse.protobuf.junit.util.Finder.findProperty;
import static com.google.eclipse.protobuf.protobuf.ProtobufPackage.Literals.FIELD__NAME;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;

import com.google.eclipse.protobuf.junit.core.XtextRule;
import com.google.eclipse.protobuf.protobuf.*;

import org.eclipse.emf.ecore.*;
import org.eclipse.xtext.nodemodel.INode;
import org.junit.*;

/**
 * Tests for <code>{@link ModelNodes#firstNodeForFeature(EObject, EStructuralFeature)}</code>
 * 
 * @author alruiz@google.com (Alex Ruiz)
 */
public class ModelNodes_firstNodeForFeature_Test {

  @Rule public XtextRule xtext = new XtextRule();

  private ModelNodes nodes;
  
  @Before public void setUp() {
    nodes = xtext.getInstanceOf(ModelNodes.class);
  }
  
  @Test public void should_return_first_node_for_feature() {
    StringBuilder proto = new StringBuilder();
    proto.append("message Person {           ")
         .append("  optional bool active = 1;")
         .append("}                          ");
    Protobuf root = xtext.parse(proto);
    Property active = findProperty("active", root);
    INode node = nodes.firstNodeForFeature(active, FIELD__NAME);
    assertThat(node.getText().trim(), equalTo("active"));
  }
}
