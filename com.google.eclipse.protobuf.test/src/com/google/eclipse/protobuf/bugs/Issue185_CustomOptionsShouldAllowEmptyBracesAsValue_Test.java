/*
 * Copyright (c) 2011 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.bugs;

import static org.junit.Assert.assertTrue;

import static com.google.eclipse.protobuf.junit.core.IntegrationTestModule.integrationTestModule;
import static com.google.eclipse.protobuf.junit.core.XtextRule.overrideRuntimeModuleWith;

import org.junit.Rule;
import org.junit.Test;

import com.google.eclipse.protobuf.junit.core.XtextRule;
import com.google.eclipse.protobuf.protobuf.ComplexValue;
import com.google.eclipse.protobuf.protobuf.CustomFieldOption;
import com.google.eclipse.protobuf.protobuf.Value;

/**
 * Tests fix for <a href="http://code.google.com/p/protobuf-dt/issues/detail?id=185">Issue 185</a>.
 *
 * @author alruiz@google.com (Alex Ruiz)
 */
public class Issue185_CustomOptionsShouldAllowEmptyBracesAsValue_Test {
  @Rule public XtextRule xtext = overrideRuntimeModuleWith(integrationTestModule());

  // syntax = "proto2";
  //
  // import "google/protobuf/descriptor.proto";
  //
  // package google.proto.test;
  //
  // message Aggregate {
  //   optional string s = 1;
  //   optional google.protobuf.FileOptions file = 2;
  // }
  //
  // extend google.protobuf.FieldOptions {
  //   optional Aggregate fileopt = 15478479;
  // }
  //
  // message Test {
  //   optional string name = 1 [(fileopt) = {}];
  // }
  @Test public void should_allow_empty_braces_as_value() {
    CustomFieldOption option = xtext.find("fileopt", ")", CustomFieldOption.class);
    Value value = option.getValue();
    ComplexValue complexValue = value instanceof ComplexValue ? (ComplexValue) value : null;
    assertTrue(complexValue != null && complexValue.getFields().isEmpty());
  }
}
