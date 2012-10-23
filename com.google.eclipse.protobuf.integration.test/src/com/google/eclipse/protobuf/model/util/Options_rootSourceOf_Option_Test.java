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
import com.google.eclipse.protobuf.protobuf.MessageField;
import com.google.eclipse.protobuf.protobuf.Option;
import com.google.inject.Inject;

/**
 * Tests for <code>{@link Options#rootSourceOf(AbstractOption)}</code>.
 *
 * @author alruiz@google.com (Alex Ruiz)
 */
public class Options_rootSourceOf_Option_Test {
  @Rule public XtextRule xtext = overrideRuntimeModuleWith(integrationTestModule());

  @Inject private Options options;

  // syntax = "proto2";
  //
  // option java_package = 'com.google.eclipse.protobuf.tests';
  @Test public void should_return_source_of_native_option() {
    Option option = xtext.find("java_package", Option.class);
    MessageField field = (MessageField) options.rootSourceOf(option);
    assertThat(field.getName(), equalTo("java_package"));
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
    Option option = xtext.find("encoding", ")", Option.class);
    MessageField field = (MessageField) options.rootSourceOf(option);
    assertThat(field.getName(), equalTo("encoding"));
  }
}
