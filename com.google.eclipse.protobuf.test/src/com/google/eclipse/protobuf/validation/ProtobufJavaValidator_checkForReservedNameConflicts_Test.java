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

import java.util.List;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.xtext.validation.ValidationMessageAcceptor;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import com.google.eclipse.protobuf.junit.core.XtextRule;
import com.google.eclipse.protobuf.protobuf.Group;
import com.google.eclipse.protobuf.protobuf.Message;
import com.google.eclipse.protobuf.protobuf.MessageField;
import com.google.eclipse.protobuf.protobuf.ProtobufPackage;
import com.google.eclipse.protobuf.protobuf.StringLiteral;
import com.google.inject.Inject;

public class ProtobufJavaValidator_checkForReservedNameConflicts_Test {
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
  //   reserved "foo", "bar";
  //   reserved "foo", 'b' 'a' 'r';
  // }
  @Test public void should_error_on_conflict_between_reserved_and_reserved() {
    validator.checkForReservedNameConflicts(xtext.findFirst(Message.class));
    List<StringLiteral> stringLiterals = xtext.findAll(StringLiteral.class);
    verifyError("\"foo\" conflicts with reserved \"foo\"", stringLiterals.get(3));
    verifyError("\"bar\" conflicts with reserved \"bar\"", stringLiterals.get(4));
  }

  // syntax = "proto2";
  //
  // message Person {
  //   reserved "foo", "bar", "baz";
  //   optional bool foo = 1;
  //   group bar = 2 {
  //     optional bool baz = 3;
  //   }
  // }
  @Test public void should_error_on_conflict_between_reserved_and_indexed_element() {
    validator.checkForReservedNameConflicts(xtext.findFirst(Message.class));
    verifyError(
        "\"foo\" conflicts with reserved \"foo\"",
        xtext.findAll(MessageField.class).get(0),
        ProtobufPackage.Literals.MESSAGE_FIELD__NAME);
    verifyError(
        "\"bar\" conflicts with reserved \"bar\"",
        xtext.findAll(Group.class).get(0),
        ProtobufPackage.Literals.COMPLEX_TYPE__NAME);
    verifyError(
        "\"baz\" conflicts with reserved \"baz\"",
        xtext.findAll(MessageField.class).get(1),
        ProtobufPackage.Literals.MESSAGE_FIELD__NAME);
  }

  private void verifyError(String message, EObject errorSource) {
    verifyError(message, errorSource, null);
  }

  private void verifyError(String message, EObject errorSource, EStructuralFeature errorFeature) {
    verify(messageAcceptor).acceptError(message, errorSource, errorFeature, -1, null);
  }
}
