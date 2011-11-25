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
import static org.junit.Assert.*;

import org.junit.*;

import com.google.eclipse.protobuf.junit.core.XtextRule;
import com.google.eclipse.protobuf.protobuf.MessageField;

/**
 * Tests for <code>{@link MessageFields#isBool(MessageField)}</code>.
 *
 * @author alruiz@google.com (Alex Ruiz)
 */
public class MessageFields_isBool_Test {

  @Rule public XtextRule xtext = createWith(unitTestSetup());

  private MessageFields fields;

  @Before public void setUp() {
    fields = xtext.getInstanceOf(MessageFields.class);
  }

  // syntax = "proto2";
  //
  // message Person {
  //   optional bool code = 1;
  // }
  @Test public void should_return_true_if_field_is_bool() {
    MessageField field = xtext.find("code", MessageField.class);
    assertTrue(fields.isBool(field));
  }

  // syntax = "proto2";
  //
  // message Person {
  //   optional string name = 1;
  // }
  @Test public void should_return_false_if_property_is_not_bool() {
    MessageField field = xtext.find("name", MessageField.class);
    assertFalse(fields.isBool(field));
  }
}
