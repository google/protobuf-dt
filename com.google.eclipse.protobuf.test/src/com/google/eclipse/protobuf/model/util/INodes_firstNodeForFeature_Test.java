/*
 * Copyright (c) 2011 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.model.util;

import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;

import static com.google.eclipse.protobuf.junit.core.UnitTestModule.unitTestModule;
import static com.google.eclipse.protobuf.junit.core.XtextRule.overrideRuntimeModuleWith;
import static com.google.eclipse.protobuf.protobuf.ProtobufPackage.Literals.MESSAGE_FIELD__NAME;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.xtext.nodemodel.INode;
import org.junit.Rule;
import org.junit.Test;

import com.google.eclipse.protobuf.junit.core.XtextRule;
import com.google.eclipse.protobuf.protobuf.MessageField;
import com.google.inject.Inject;

/**
 * Tests for <code>{@link INodes#firstNodeForFeature(EObject, EStructuralFeature)}</code>
 *
 * @author alruiz@google.com (Alex Ruiz)
 */
public class INodes_firstNodeForFeature_Test {
  @Rule public XtextRule xtext = overrideRuntimeModuleWith(unitTestModule());

  @Inject private INodes nodes;

  // syntax = "proto2";
  //
  // message Person {
  //   optional bool active = 1;
  // }
  @Test public void should_return_first_node_for_feature() {
    MessageField field = xtext.find("active", MessageField.class);
    INode node = nodes.firstNodeForFeature(field, MESSAGE_FIELD__NAME);
    assertThat(node.getText().trim(), equalTo("active"));
  }
}
