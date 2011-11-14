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
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;

import org.junit.*;

import com.google.eclipse.protobuf.junit.core.XtextRule;
import com.google.eclipse.protobuf.protobuf.MessageField;

/**
 * Tests for <code>{@link Fields#typeNameOf(MessageField)}</code>.
 *
 * @author alruiz@google.com (Alex Ruiz)
 */
public class Properties_typeNameOf_Test {

  @Rule public XtextRule xtext = createWith(unitTestSetup());

  private Fields properties;

  @Before public void setUp() {
    properties = xtext.getInstanceOf(Fields.class);
  }

  // syntax = "proto2";
  //
  // message Person {
  //   optional string name = 1;
  // }
  @Test public void should_return_name_of_scalar() {
    MessageField field = xtext.find("name", MessageField.class);
    assertThat(properties.typeNameOf(field), equalTo("string"));
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
    assertThat(properties.typeNameOf(field), equalTo("PhoneNumber"));
  }
}
