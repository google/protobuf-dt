/*
 * Copyright (c) 2013 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.bugs;

import static org.junit.Assert.assertTrue;

import static com.google.eclipse.protobuf.junit.core.UnitTestModule.unitTestModule;
import static com.google.eclipse.protobuf.junit.core.XtextRule.overrideRuntimeModuleWith;

import org.eclipse.emf.common.util.BasicDiagnostic;
import org.junit.Rule;
import org.junit.Test;

import com.google.eclipse.protobuf.junit.core.XtextRule;
import com.google.eclipse.protobuf.validation.ProtobufJavaValidator;
import com.google.inject.Inject;

/**
 * Tests fix for b/10744621.
 */
public class b10744621_AllowSemicolonAfterOptionValue_Test {
  @Rule public XtextRule xtext = overrideRuntimeModuleWith(unitTestModule());

  @Inject private ProtobufJavaValidator validator;

  // syntax = "proto2";
  //
  // package abc;
  //
  // message TestValues {
  //   optional string value1 = 1;
  //   optional string value2 = 2;
  // }
  // 
  // extend google.protobuf.MessageOptions {
  //   optional TestValues test_values = 42180343;
  // }
  // 
  // message TestMessage {
  //   option (test_values) = {
  //     value1: "Value 1";
  //     value2: "Value 2"
  //   };
  // }
  @Test public void should_allow_semicolon_in_complex_value() {
    boolean isValid = validator.validate(xtext.root(), new BasicDiagnostic(), null);
    assertTrue(isValid);
  }
}
