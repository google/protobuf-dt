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

import static com.google.eclipse.protobuf.junit.core.IntegrationTestModule.integrationTestModule;
import static com.google.eclipse.protobuf.junit.core.XtextRule.overrideRuntimeModuleWith;

import org.junit.Rule;
import org.junit.Test;

import com.google.eclipse.protobuf.junit.core.XtextRule;
import com.google.eclipse.protobuf.protobuf.AbstractOption;
import com.google.eclipse.protobuf.protobuf.FieldOption;
import com.google.eclipse.protobuf.protobuf.MessageField;
import com.google.inject.Inject;

/**
 * Tests for <code>{@link Options#rootSourceOf(AbstractOption)}</code>.
 *
 * @author alruiz@google.com (Alex Ruiz)
 */
public class Options_rootSourceOf_FieldOption_Test {
  @Rule public XtextRule xtext = overrideRuntimeModuleWith(integrationTestModule());

  @Inject private Options options;

  // syntax = "proto2";
  //
  // message Person {
  //   optional boolean active = 1 [deprecated = false];
  // }
  @Test public void should_return_field_of_native_field_option() {
    FieldOption option = xtext.find("deprecated", FieldOption.class);
    MessageField field = (MessageField) options.rootSourceOf(option);
    assertThat(field.getName(), equalTo("deprecated"));
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
  @Test public void should_return_field_of_custom_field_option() {
    FieldOption option = xtext.find("encoding", ")", FieldOption.class);
    MessageField field = (MessageField) options.rootSourceOf(option);
    assertThat(field.getName(), equalTo("encoding"));
  }
}
