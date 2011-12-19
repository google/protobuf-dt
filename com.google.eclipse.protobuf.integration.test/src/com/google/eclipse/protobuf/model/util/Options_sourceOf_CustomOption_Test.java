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
 * Tests for <code>{@link Options#sourceOf(CustomOption)}</code>
 *
 * @author alruiz@google.com (Alex Ruiz)
 */
public class Options_sourceOf_CustomOption_Test {

  @Rule public XtextRule xtext = createWith(integrationTestSetup());

  private Options options;

  @Before public void setUp() {
    options = xtext.getInstanceOf(Options.class);
  }

  // syntax = "proto2";
  //
  // import 'google/protobuf/descriptor.proto';
  //
  // extend google.protobuf.FileOptions {
  //   optional string encoding = 1000;
  // }
  //
  // option (encoding) = 'UTF-8';
  @Test public void should_return_source_of_custom_option() {
    CustomOption option = xtext.find("encoding", ")", CustomOption.class);
    MessageField p = (MessageField) options.sourceOf(option);
    assertThat(p.getName(), equalTo("encoding"));
  }

  // syntax = "proto2";
  //
  // import 'google/protobuf/descriptor.proto';
  //
  // message Custom {
  //   optional int32 count = 1;
  // }
  //
  // extend google.protobuf.FileOptions {
  //   optional Custom custom = 1000;
  // }
  //
  // option (custom).count = 6;
  @Test public void should_return_source_of_field_in_option() {
    CustomOption option = xtext.find("custom", ")", CustomOption.class);
    MessageField p = (MessageField) options.sourceOf(option);
    assertThat(p.getName(), equalTo("count"));
  }
}
