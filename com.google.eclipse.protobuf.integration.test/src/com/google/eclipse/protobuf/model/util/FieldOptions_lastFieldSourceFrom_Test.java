/*
 * Copyright (c) 2011 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.model.util;

import static com.google.eclipse.protobuf.junit.core.Setups.integrationTestSetup;
import static com.google.eclipse.protobuf.junit.core.XtextRule.createWith;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;

import org.junit.*;

import com.google.eclipse.protobuf.junit.core.XtextRule;
import com.google.eclipse.protobuf.protobuf.*;

/**
 * Tests for <code>{@link FieldOptions#lastFieldSourceFrom(CustomFieldOption)}</code>.
 *
 * alruiz@google.com (Alex Ruiz)
 */
public class FieldOptions_lastFieldSourceFrom_Test {

  @Rule public XtextRule xtext = createWith(integrationTestSetup());

  private FieldOptions fieldOptions;

  @Before public void setUp() {
    fieldOptions = xtext.getInstanceOf(FieldOptions.class);
  }

  // syntax = "proto2";
  //
  // import 'google/protobuf/descriptor.proto';
  //
  // message Custom {
  //   optional int32 count = 1;
  // }
  //
  // extend google.protobuf.FieldOptions {
  //   optional Custom custom = 1000;
  // }
  //
  // message Person {
  //   optional boolean active = 1 [(custom).count = 6];
  // }
  @Test public void should_return_property_field() {
    CustomFieldOption option = xtext.find("custom", ").", CustomFieldOption.class);
    Property p = (Property) fieldOptions.lastFieldSourceFrom(option);
    assertThat(p.getName(), equalTo("count"));
  }
}
