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
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;

import static com.google.eclipse.protobuf.junit.core.UnitTestModule.unitTestModule;
import static com.google.eclipse.protobuf.junit.core.XtextRule.overrideRuntimeModuleWith;

import org.junit.Rule;
import org.junit.Test;

import com.google.eclipse.protobuf.junit.core.XtextRule;
import com.google.eclipse.protobuf.protobuf.MessageField;
import com.google.eclipse.protobuf.protobuf.ScalarType;
import com.google.inject.Inject;

/**
 * Tests for <code>{@link MessageFields#scalarTypeOf(MessageField)}</code>.
 *
 * @author alruiz@google.com (Alex Ruiz)
 */
public class MessageFields_scalarTypeOf_Test {
  @Rule public XtextRule xtext = overrideRuntimeModuleWith(unitTestModule());

  @Inject private MessageFields fields;

  // syntax = "proto2";
  //
  // message Person {
  //   optional int32 id = 1;
  // }
  @Test public void should_return_scalar_if_field_type_is_scalar() {
    MessageField field = xtext.find("id", MessageField.class);
    ScalarType type = fields.scalarTypeOf(field);
    assertThat(type.getName(), equalTo("int32"));
  }

  // syntax = "proto2";
  //
  // enum PhoneType {
  //   MOBILE = 0;
  //   HOME = 1;
  //   WORK = 2;
  // }
  //
  // message PhoneNumber {
  //   optional PhoneType type = 1;
  // }
  @Test public void should_return_null_if_field_type_is_not_scalar() {
    MessageField field = xtext.find("type", MessageField.class);
    assertNull(fields.scalarTypeOf(field));
  }
}
