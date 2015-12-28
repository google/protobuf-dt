/*
 * Copyright (c) 2015 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.model.util;

import static org.hamcrest.core.IsEqual.equalTo;
import static org.hamcrest.core.IsNull.nullValue;
import static org.junit.Assert.assertThat;
import static com.google.eclipse.protobuf.junit.core.UnitTestModule.unitTestModule;
import static com.google.eclipse.protobuf.junit.core.XtextRule.overrideRuntimeModuleWith;

import org.junit.Rule;
import org.junit.Test;

import com.google.eclipse.protobuf.junit.core.XtextRule;
import com.google.eclipse.protobuf.protobuf.MessageField;
import com.google.inject.Inject;

/**
 * Tests for <code>{@link MessageFields#messageTypeOf(MessageField)}</code>.
 *
 * @author jogl@google.com (John Glassmyer)
 */
public class MessageFields_messageTypeOf_Test {
  @Rule public XtextRule xtext = overrideRuntimeModuleWith(unitTestModule());

  @Inject private MessageFields fields;

  // syntax = "proto2";
  //
  // message Person {
  //   optional string name = 1;
  // }
  @Test public void should_return_null_for_scalar() {
    MessageField field = xtext.find("name", MessageField.class);
    assertThat(fields.messageTypeOf(field), nullValue());
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
  @Test public void should_return_name_of_message() {
    MessageField field = xtext.find("number", MessageField.class);
    assertThat(fields.messageTypeOf(field).getName(), equalTo("PhoneNumber"));
  }

  // syntax = "proto2";
  //
  // message Person {
  //   optional map<string, PhoneNumber> phone_book = 1;
  //
  //   message PhoneNumber {
  //     optional string value = 1;
  //   }
  // }
  @Test public void should_return_null_for_map() {
    MessageField field = xtext.find("phone_book", MessageField.class);
    assertThat(fields.messageTypeOf(field), nullValue());
  }
}
