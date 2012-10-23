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

import org.junit.Rule;
import org.junit.Test;

import com.google.eclipse.protobuf.junit.core.XtextRule;
import com.google.eclipse.protobuf.protobuf.MessageField;
import com.google.inject.Inject;

/**
 * Tests for <code>{@link MessageFields#typeNameOf(MessageField)}</code>.
 *
 * @author alruiz@google.com (Alex Ruiz)
 */
public class MessageFields_typeNameOf_Test {
  @Rule public XtextRule xtext = overrideRuntimeModuleWith(unitTestModule());

  @Inject private MessageFields fields;

  // syntax = "proto2";
  //
  // message Person {
  //   optional string name = 1;
  // }
  @Test public void should_return_name_of_scalar() {
    MessageField field = xtext.find("name", MessageField.class);
    assertThat(fields.typeNameOf(field), equalTo("string"));
  }

  // syntax = "proto2";
  //
  // message Person {
  //   optional string name = 1;
  //   optional PhoneNumber number = 2;
  //
  //   message PhoneNumber {
  //     optional string value = 1;
  //   }
  // }
  @Test public void should_return_name_of_type() {
    MessageField field = xtext.find("number", MessageField.class);
    assertThat(fields.typeNameOf(field), equalTo("PhoneNumber"));
  }
}
