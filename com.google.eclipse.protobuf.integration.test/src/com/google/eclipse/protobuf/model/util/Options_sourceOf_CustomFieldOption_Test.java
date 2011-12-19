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
 * Tests for <code>{@link Options#sourceOf(CustomFieldOption)}</code>.
 *
 * @author alruiz@google.com (Alex Ruiz)
 */
public class Options_sourceOf_CustomFieldOption_Test {

  @Rule public XtextRule xtext = createWith(integrationTestSetup());

  private Options options;

  @Before public void setUp() {
    options = xtext.getInstanceOf(Options.class);
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
  @Test public void should_return_source_of_field_option() {
    CustomFieldOption option = xtext.find("encoding", ")", CustomFieldOption.class);
    MessageField field = (MessageField) options.sourceOf(option);
    assertThat(field.getName(), equalTo("encoding"));
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
  @Test public void should_return_source_of_field_in_field_option() {
    CustomFieldOption option = xtext.find("custom", ").", CustomFieldOption.class);
    MessageField field = (MessageField) options.sourceOf(option);
    assertThat(field.getName(), equalTo("count"));
  }
}
