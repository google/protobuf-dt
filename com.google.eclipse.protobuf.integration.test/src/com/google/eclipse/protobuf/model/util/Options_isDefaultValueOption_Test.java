/*
 * Copyright (c) 2011 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.model.util;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import static com.google.eclipse.protobuf.junit.core.IntegrationTestModule.integrationTestModule;
import static com.google.eclipse.protobuf.junit.core.XtextRule.overrideRuntimeModuleWith;

import org.junit.Rule;
import org.junit.Test;

import com.google.eclipse.protobuf.junit.core.XtextRule;
import com.google.eclipse.protobuf.protobuf.FieldOption;
import com.google.inject.Inject;

/**
 * Tests for <code>{@link Options#isDefaultValueOption(FieldOption)}</code>.
 *
 * @author alruiz@google.com (Alex Ruiz)
 */
public class Options_isDefaultValueOption_Test {
  @Rule public XtextRule xtext = overrideRuntimeModuleWith(integrationTestModule());

  @Inject private Options options;

  // syntax = "proto2";
  //
  // message Person {
  //   optional boolean active = 1 [default = true, deprecated = false];
  // }
  @Test public void should_return_true_if_FieldOption_is_default_value_one() {
    FieldOption option = xtext.find("default", FieldOption.class);
    assertTrue(options.isDefaultValueOption(option));
  }

  // syntax = "proto2";
  //
  // message Person {
  //   optional boolean active = 1 [default = true, deprecated = false];
  // }
  @Test public void should_return_false_if_FieldOption_is_not_default_value_one() {
    FieldOption option = xtext.find("deprecated", FieldOption.class);
    assertFalse(options.isDefaultValueOption(option));
  }
}
