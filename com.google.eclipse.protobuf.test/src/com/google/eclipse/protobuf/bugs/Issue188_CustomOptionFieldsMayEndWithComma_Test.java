/*
 * Copyright (c) 2011 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.bugs;

import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;

import static com.google.eclipse.protobuf.junit.core.IntegrationTestModule.integrationTestModule;
import static com.google.eclipse.protobuf.junit.core.XtextRule.overrideRuntimeModuleWith;

import java.util.List;

import org.junit.Rule;
import org.junit.Test;

import com.google.eclipse.protobuf.junit.core.XtextRule;
import com.google.eclipse.protobuf.protobuf.ComplexValue;
import com.google.eclipse.protobuf.protobuf.CustomOption;
import com.google.eclipse.protobuf.protobuf.ValueField;

/**
 * Tests fix for <a href="http://code.google.com/p/protobuf-dt/issues/detail?id=188">Issue 188</a>.
 *
 * @author alruiz@google.com (Alex Ruiz)
 */
public class Issue188_CustomOptionFieldsMayEndWithComma_Test {
  @Rule public XtextRule xtext = overrideRuntimeModuleWith(integrationTestModule());

  // syntax = "proto2";
  //
  // import "google/protobuf/descriptor.proto";
  //
  // extend google.protobuf.MessageOptions {
  //   optional ValuesOption values = 20241259;
  // }
  //
  // message ValuesOption {
  //   repeated Value value = 1;
  // }
  //
  // message Value {
  //   required string name = 1;
  // }
  //
  // message Test {
  //   option (values) = {
  //     value: { name: "Common" },
  //     value: { name: "External" },
  //     value: { name: "Error" },
  //   };
  // }
  @Test public void should_support_comma_after_each_option_field() {
    CustomOption option = xtext.find("values", ")", CustomOption.class);
    ComplexValue value = (ComplexValue) option.getValue();
    List<ValueField> fields = value.getFields();
    assertThat(fields.size(), equalTo(3));
  }
}
