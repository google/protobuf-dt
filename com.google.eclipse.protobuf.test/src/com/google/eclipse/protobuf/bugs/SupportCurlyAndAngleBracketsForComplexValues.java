/*
 * Copyright (c) 2013 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.bugs;

import static com.google.eclipse.protobuf.junit.core.UnitTestModule.unitTestModule;
import static com.google.eclipse.protobuf.junit.core.XtextRule.overrideRuntimeModuleWith;
import static org.junit.Assert.assertTrue;

import com.google.eclipse.protobuf.junit.core.XtextRule;
import com.google.eclipse.protobuf.validation.ProtobufJavaValidator;
import com.google.inject.Inject;

import org.eclipse.emf.common.util.BasicDiagnostic;
import org.junit.Rule;
import org.junit.Test;

/**
 * Test that complex values can be enclosed in {...} or &lt;...&gt;
 */
public class SupportCurlyAndAngleBracketsForComplexValues {
  @Rule public XtextRule xtext = overrideRuntimeModuleWith(unitTestModule());

  @Inject private ProtobufJavaValidator validator;

  //  syntax = "proto2";
  //
  //  package test;
  //
  //  message AttributeOptions {
  //    message CommonAttributes {
  //      optional bool boolValue = 1;
  //      optional int32 intValue = 2;
  //    }
  //
  //    optional group MainAttribute = 2 {
  //      optional CommonAttributes attrs = 5;
  //    }
  //
  //    optional group ExtraAttribute = 6 {
  //      optional CommonAttributes attrs = 7;
  //    }
  //
  //    optional int32 value = 8;
  //  }
  //
  //  extend google.protobuf.MessageOptions {
  //    optional AttributeOptions main_options = 16662875;
  //    optional AttributeOptions.CommonAttributes common_options = 16662876;
  //  }
  //
  //  message TestMessage {
  //    option (main_options) = {
  //      value: 10,
  //
  //      MainAttribute <
  //        attrs {
  //          boolValue: false
  //        }
  //      >
  //
  //      ExtraAttribute {
  //        attrs {
  //          boolValue: false
  //        }
  //      }
  //    };
  //
  //    option (.test.common_options) = {
  //      intValue: 3
  //    };
  //  }
  @Test public void should_allow_curly_and_angle_brackets_in_complex_value() {
    boolean isValid = validator.validate(xtext.root(), new BasicDiagnostic(), null);
    assertTrue(isValid);
  }
}
