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
import com.google.eclipse.protobuf.protobuf.IndexRange;
import com.google.eclipse.protobuf.protobuf.Message;
import com.google.eclipse.protobuf.protobuf.MessageField;
import com.google.eclipse.protobuf.protobuf.ProtobufPackage;
import com.google.inject.Inject;

public class ProtobufJavaValidator_checkForIndexRangeConflicts_Test {
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
  //   reserved 10, 20 to 30;
  //   reserved 10;
  //   reserved 20;
  //   reserved 25 to 26;
  //   reserved 30 to 31;
  // }
  @Test public void should_error_on_conflict_between_reserved_and_reserved() {
    validator.checkForIndexRangeConflicts(xtext.findFirst(Message.class));
    List<IndexRange> indexRanges = xtext.findAll(IndexRange.class);
    verifyError("10 conflicts with reserved 10", indexRanges.get(2));
    verifyError("20 conflicts with reserved 20 to 30", indexRanges.get(3));
    verifyError("25 to 26 conflicts with reserved 20 to 30", indexRanges.get(4));
    verifyError("30 to 31 conflicts with reserved 20 to 30", indexRanges.get(5));
  }

  // syntax = "proto2";
  //
  // message Person {
  //   reserved 10, 20 to 30;
  //   extensions 10, 15 to max;
  // }
  @Test public void should_error_on_conflict_between_reserved_and_extensions() {
    validator.checkForIndexRangeConflicts(xtext.findFirst(Message.class));
    List<IndexRange> indexRanges = xtext.findAll(IndexRange.class);
    verifyError("10 conflicts with reserved 10", indexRanges.get(2));
    verifyError("15 to max conflicts with reserved 20 to 30", indexRanges.get(3));
  }

  // syntax = "proto2";
  //
  // message Person {
  //   reserved 10, 20 to 30;
  //   optional bool foo = 10;
  //   optional bool bar = 25;
  //   group baz = 26 {
  //     optional bool a = 27;
  //   }
  // }
  @Test public void should_error_on_conflict_between_reserved_and_indexed_element() {
    validator.checkForIndexRangeConflicts(xtext.findFirst(Message.class));
    verifyError(
        "10 conflicts with reserved 10",
        xtext.findAll(MessageField.class).get(0),
        ProtobufPackage.Literals.MESSAGE_FIELD__INDEX);
    verifyError(
        "25 conflicts with reserved 20 to 30",
        xtext.findAll(MessageField.class).get(1),
        ProtobufPackage.Literals.MESSAGE_FIELD__INDEX);
    verifyError(
        "26 conflicts with reserved 20 to 30",
        xtext.findAll(Group.class).get(0),
        ProtobufPackage.Literals.GROUP__INDEX);
    verifyError(
        "27 conflicts with reserved 20 to 30",
        xtext.findAll(MessageField.class).get(2),
        ProtobufPackage.Literals.MESSAGE_FIELD__INDEX);
  }

  // syntax = "proto2";
  //
  // message Person {
  //   extensions 10, 20 to 30;
  //   optional bool foo = 10;
  //   optional bool bar = 25;
  //   group baz = 26 {
  //     optional bool a = 27;
  //   }
  // }
  @Test public void should_error_on_conflict_between_extensions_and_indexed_element() {
    validator.checkForIndexRangeConflicts(xtext.findFirst(Message.class));
    verifyError(
        "10 conflicts with extensions 10",
        xtext.findAll(MessageField.class).get(0),
        ProtobufPackage.Literals.MESSAGE_FIELD__INDEX);
    verifyError(
        "25 conflicts with extensions 20 to 30",
        xtext.findAll(MessageField.class).get(1),
        ProtobufPackage.Literals.MESSAGE_FIELD__INDEX);
    verifyError(
        "26 conflicts with extensions 20 to 30",
        xtext.findAll(Group.class).get(0),
        ProtobufPackage.Literals.GROUP__INDEX);
    verifyError(
        "27 conflicts with extensions 20 to 30",
        xtext.findAll(MessageField.class).get(2),
        ProtobufPackage.Literals.MESSAGE_FIELD__INDEX);
  }

  private void verifyError(String message, EObject errorSource) {
    verifyError(message, errorSource, null);
  }

  private void verifyError(String message, EObject errorSource, EStructuralFeature errorFeature) {
    verify(messageAcceptor).acceptError(message, errorSource, errorFeature, -1, null);
  }
}
