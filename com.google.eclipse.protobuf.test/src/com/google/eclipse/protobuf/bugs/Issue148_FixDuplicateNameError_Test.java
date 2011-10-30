/*
 * Copyright (c) 2011 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.bugs;

import static com.google.eclipse.protobuf.junit.core.Setups.unitTestSetup;
import static com.google.eclipse.protobuf.junit.core.XtextRule.createWith;
import static org.junit.Assert.assertTrue;

import org.eclipse.emf.common.util.BasicDiagnostic;
import org.junit.*;

import com.google.eclipse.protobuf.junit.core.XtextRule;
import com.google.eclipse.protobuf.validation.ProtobufJavaValidator;

/**
 * Tests fix for <a href="http://code.google.com/p/protobuf-dt/issues/detail?id=91">Issue 91</a>.
 *
 * @author alruiz@google.com (Alex Ruiz)
 */
public class Issue148_FixDuplicateNameError_Test {

  @Rule public XtextRule xtext = createWith(unitTestSetup());
  
  private ProtobufJavaValidator validator;
  
  @Before public void setUp() {
    validator = xtext.getInstanceOf(ProtobufJavaValidator.class);
  }
  
  // package abc;
  //
  // message abc {}
  @Test public void should_allow_elements_of_different_types_have_same_name() {
    boolean isValid = validator.validate(xtext.root(), new BasicDiagnostic(), null);
    assertTrue(isValid);
  }
}
