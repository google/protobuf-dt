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

import static com.google.eclipse.protobuf.junit.core.UnitTestModule.unitTestModule;
import static com.google.eclipse.protobuf.junit.core.XtextRule.overrideRuntimeModuleWith;

import org.eclipse.emf.common.util.BasicDiagnostic;
import org.junit.Rule;
import org.junit.Test;

import com.google.eclipse.protobuf.junit.core.XtextRule;
import com.google.eclipse.protobuf.validation.ProtobufJavaValidator;
import com.google.inject.Inject;

/**
 * Tests fix for <a href="http://code.google.com/p/protobuf-dt/issues/detail?id=91">Issue 91</a>.
 *
 * @author alruiz@google.com (Alex Ruiz)
 */
public class Issue148_FixDuplicateNameError_Test {
  @Rule public XtextRule xtext = overrideRuntimeModuleWith(unitTestModule());

  @Inject private ProtobufJavaValidator validator;

  // syntax = "proto2";
  //
  // package abc;
  //
  // message abc {}
  @Test public void should_allow_elements_of_different_types_have_same_name() {
    boolean isValid = validator.validate(xtext.root(), new BasicDiagnostic(), null);
    assertTrue(isValid);
  }
}
