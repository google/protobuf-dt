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
import com.google.eclipse.protobuf.protobuf.AbstractCustomOption;
import com.google.eclipse.protobuf.protobuf.CustomOption;
import com.google.eclipse.protobuf.protobuf.MessageField;
import com.google.inject.Inject;

/**
 * Tests for <code>{@link Options#sourceOfLastFieldIn(AbstractCustomOption)}</code>.
 *
 * alruiz@google.com (Alex Ruiz)
 */
public class Options_sourceOfLastFieldIn_CustomOption_Test {
  @Rule public XtextRule xtext = overrideRuntimeModuleWith(integrationTestModule());

  @Inject private Options options;

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
  @Test public void should_return_option_field() {
    CustomOption option = xtext.find("custom", ")", CustomOption.class);
    MessageField field = (MessageField) options.sourceOfLastFieldIn(option);
    assertThat(field.getName(), equalTo("count"));
  }
}
