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
import com.google.eclipse.protobuf.protobuf.FieldOption;

/**
 * Tests for <code>{@link FieldOptions#nameOf(FieldOption)}</code>.
 *
 * @author alruiz@google.com (Alex Ruiz)
 */
public class FieldOptions_nameOf_Test {

  @Rule public XtextRule xtext = createWith(integrationTestSetup());

  private FieldOptions fieldOptions;

  @Before public void setUp() {
    fieldOptions = xtext.getInstanceOf(FieldOptions.class);
  }

  // syntax = "proto2";
  //
  // message Person {
  //   optional boolean active = 1 [deprecated = false];
  // }
  @Test public void should_return_name_of_native_field_option() {
    FieldOption option = xtext.find("deprecated", FieldOption.class);
    String name = fieldOptions.nameOf(option);
    assertThat(name, equalTo("deprecated"));
  }

  // syntax = "proto2";
  //
  // import 'google/protobuf/descriptor.proto';
  //
  // extend google.protobuf.FieldOptions {
  //   optional string encoding = 1000;
  // }
  //
  // message Person {
  //   optional boolean active = 1 [(encoding) = 'UTF-8'];
  // }
  @Test public void should_return_name_of_custom_field_option() {
    FieldOption option = xtext.find("encoding", ")", FieldOption.class);
    String name = fieldOptions.nameOf(option);
    assertThat(name, equalTo("encoding"));
  }
}
