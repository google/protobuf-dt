/*
 * Copyright (c) 2011 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.model.util;

import static com.google.eclipse.protobuf.junit.core.IntegrationTestModule.integrationTestModule;
import static com.google.eclipse.protobuf.junit.core.XtextRule.overrideRuntimeModuleWith;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;

import org.junit.*;

import com.google.eclipse.protobuf.junit.core.XtextRule;
import com.google.eclipse.protobuf.protobuf.*;

/**
 * Tests for <code>{@link Options#sourceOfLastFieldIn(AbstractCustomOption)}</code>.
 *
 * alruiz@google.com (Alex Ruiz)
 */
public class Options_sourceOfLastFieldIn_CustomFieldOption_Test {
  @Rule public XtextRule xtext = overrideRuntimeModuleWith(integrationTestModule());

  private Options fieldOptions;

  @Before public void setUp() {
    fieldOptions = xtext.getInstanceOf(Options.class);
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
  @Test public void should_return_option_field() {
    CustomFieldOption option = xtext.find("custom", ").", CustomFieldOption.class);
    MessageField field = (MessageField) fieldOptions.sourceOfLastFieldIn(option);
    assertThat(field.getName(), equalTo("count"));
  }
}
