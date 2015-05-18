/*
 * Copyright (c) 2015 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.validation;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static com.google.eclipse.protobuf.junit.core.UnitTestModule.unitTestModule;
import static com.google.eclipse.protobuf.junit.core.XtextRule.overrideRuntimeModuleWith;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.xtext.validation.ValidationMessageAcceptor;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import com.google.eclipse.protobuf.junit.core.XtextRule;
import com.google.eclipse.protobuf.protobuf.Reserved;
import com.google.inject.Inject;

public class ProtobufJavaValidator_checkForReservedIndexAndName_Test {
  private static final String ERROR_MESSAGE = "A reserved declaration may not include both numbers and names.";

  @Rule public XtextRule xtext = overrideRuntimeModuleWith(unitTestModule());

  @Inject private ProtobufJavaValidator validator;
  private ValidationMessageAcceptor messageAcceptor;

  @Before public void setUp() {
    messageAcceptor = mock(ValidationMessageAcceptor.class);
    validator.setMessageAcceptor(messageAcceptor);
  }

  // syntax = "proto2";
  //
  // message Person {
  //   reserved 1, "foo";
  // }
  @Test public void should_error_on_reserved_index_and_name() {
    validator.checkForReservedIndexAndName(xtext.findFirst(Reserved.class));
    verifyError(ERROR_MESSAGE, xtext.findFirst(Reserved.class), null);
  }

  // syntax = "proto2";
  //
  // message Person {
  //   reserved "foo", 1;
  // }
  @Test public void should_error_on_reserved_name_and_index() {
    validator.checkForReservedIndexAndName(xtext.findFirst(Reserved.class));
    verifyError(ERROR_MESSAGE, xtext.findFirst(Reserved.class), null);
  }

  private void verifyError(String message, EObject errorSource, EStructuralFeature errorFeature) {
    verify(messageAcceptor).acceptError(message, errorSource, errorFeature, -1, null);
  }
}
