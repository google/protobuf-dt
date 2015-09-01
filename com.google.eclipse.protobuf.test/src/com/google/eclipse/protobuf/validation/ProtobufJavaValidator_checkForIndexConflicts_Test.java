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
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.verifyZeroInteractions;
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

public class ProtobufJavaValidator_checkForIndexConflicts_Test {
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
    validator.checkForIndexConflicts(xtext.findFirst(Message.class));
    List<IndexRange> indexRanges = xtext.findAll(IndexRange.class);
    verifyError("Tag number 10 conflicts with reserved 10.", indexRanges.get(2));
    verifyError("Tag number 20 conflicts with reserved 20 to 30.", indexRanges.get(3));
    verifyError("Tag number range 25 to 26 conflicts with reserved 20 to 30.", indexRanges.get(4));
    verifyError("Tag number range 30 to 31 conflicts with reserved 20 to 30.", indexRanges.get(5));
    verifyNoMoreInteractions(messageAcceptor);
  }

  // syntax = "proto2";
  //
  // message Person {
  //   reserved 10, 20 to 30;
  //   extensions 10, 15 to max;
  // }
  @Test public void should_error_on_conflict_between_reserved_and_extensions() {
    validator.checkForIndexConflicts(xtext.findFirst(Message.class));
    List<IndexRange> indexRanges = xtext.findAll(IndexRange.class);
    verifyError("Tag number 10 conflicts with reserved 10.", indexRanges.get(2));
    verifyError("Tag number range 15 to max conflicts with reserved 20 to 30.", indexRanges.get(3));
    verifyNoMoreInteractions(messageAcceptor);
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
    validator.checkForIndexConflicts(xtext.findFirst(Message.class));
    verifyError(
        "Tag number 10 conflicts with reserved 10.",
        xtext.findAll(MessageField.class).get(0),
        ProtobufPackage.Literals.MESSAGE_FIELD__INDEX);
    verifyError(
        "Tag number 25 conflicts with reserved 20 to 30.",
        xtext.findAll(MessageField.class).get(1),
        ProtobufPackage.Literals.MESSAGE_FIELD__INDEX);
    verifyError(
        "Tag number 26 conflicts with reserved 20 to 30.",
        xtext.findAll(Group.class).get(0),
        ProtobufPackage.Literals.GROUP__INDEX);
    verifyError(
        "Tag number 27 conflicts with reserved 20 to 30.",
        xtext.findAll(MessageField.class).get(2),
        ProtobufPackage.Literals.MESSAGE_FIELD__INDEX);
    verifyNoMoreInteractions(messageAcceptor);
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
    validator.checkForIndexConflicts(xtext.findFirst(Message.class));
    verifyError(
        "Tag number 10 conflicts with extensions 10.",
        xtext.findAll(MessageField.class).get(0),
        ProtobufPackage.Literals.MESSAGE_FIELD__INDEX);
    verifyError(
        "Tag number 25 conflicts with extensions 20 to 30.",
        xtext.findAll(MessageField.class).get(1),
        ProtobufPackage.Literals.MESSAGE_FIELD__INDEX);
    verifyError(
        "Tag number 26 conflicts with extensions 20 to 30.",
        xtext.findAll(Group.class).get(0),
        ProtobufPackage.Literals.GROUP__INDEX);
    verifyError(
        "Tag number 27 conflicts with extensions 20 to 30.",
        xtext.findAll(MessageField.class).get(2),
        ProtobufPackage.Literals.MESSAGE_FIELD__INDEX);
    verifyNoMoreInteractions(messageAcceptor);
  }

  // syntax = "proto2";
  //
  // message Person {
  //   optional bool foo1 = 1;
  //   optional bool foo2 = 1;
  //   repeated group foo3 = 1 {
  //     optional bool foo4 = 1;
  //   }
  //   oneof choose_one {
  //     optional bool foo5 = 1;
  //   }
  //
  //   repeated group foo6 = 2 {
  //     optional bool foo7 = 3;
  //   }
  //   optional bool foo8 = 2;
  // }
  @Test public void should_error_on_conflict_between_indexed_elements() {
    validator.checkForIndexConflicts(xtext.findFirst(Message.class));
    verifyError(
        "Tag number 1 conflicts with field \"foo1\".",
        xtext.findAll(MessageField.class).get(1),
        ProtobufPackage.Literals.MESSAGE_FIELD__INDEX);
    verifyError(
        "Tag number 1 conflicts with field \"foo1\".",
        xtext.findAll(Group.class).get(0),
        ProtobufPackage.Literals.GROUP__INDEX);
    verifyError(
        "Tag number 1 conflicts with field \"foo1\".",
        xtext.findAll(MessageField.class).get(2),
        ProtobufPackage.Literals.MESSAGE_FIELD__INDEX);
    verifyError(
        "Tag number 1 conflicts with field \"foo1\".",
        xtext.findAll(MessageField.class).get(3),
        ProtobufPackage.Literals.MESSAGE_FIELD__INDEX);
    verifyError(
        "Tag number 2 conflicts with group \"foo6\".",
        xtext.findAll(MessageField.class).get(5),
        ProtobufPackage.Literals.MESSAGE_FIELD__INDEX);
    verifyNoMoreInteractions(messageAcceptor);
  }

  // syntax = "proto2";
  //
  // message Car {
  // }
  //
  // message Person {
  //   extensions 10 to 20;
  //   reserved 30 to 40;
  //   optional bool foo = 50;
  //   extend Car {
  //     optional Person passenger_1 = 15;
  //     optional Person passenger_2 = 35;
  //     optional Person passenger_3 = 50;
  //   }
  // }
  @Test public void should_not_find_conflicts_with_nested_type_extension_contents() {
    validator.checkForIndexConflicts(xtext.findFirst(Message.class));
    verifyZeroInteractions(messageAcceptor);
  }

  // syntax = "proto2";
  //
  // message Person {
  //   extensions 10 to 20;
  //   reserved 30 to 40;
  //   optional bool foo = 50;
  //   message Car {
  //     optional Person passenger_1 = 15;
  //     optional Person passenger_2 = 35;
  //     optional Person passenger_3 = 50;
  //   }
  // }
  @Test public void should_not_find_conflicts_with_nested_message_contents() {
    validator.checkForIndexConflicts(xtext.findFirst(Message.class));
    verifyZeroInteractions(messageAcceptor);
  }

  private void verifyError(String message, EObject errorSource) {
    verifyError(message, errorSource, null);
  }

  private void verifyError(String message, EObject errorSource, EStructuralFeature errorFeature) {
    verify(messageAcceptor).acceptError(message, errorSource, errorFeature, -1, null);
  }

  // syntax = "proto2";
  //
  // message Person {
  //   group foo = 10 {
  //     reserved 1 to 3;
  //     optional bool in_same_group = 1;
  //   }
  //   optional bool outside_group = 2;
  //   group bar = 20 {
  //     optional bool in_other_group = 3;
  //   }
  // }
  @Test public void should_error_on_conflict_with_reserved_in_group() {
    validator.checkForIndexConflicts(xtext.findFirst(Message.class));
    List<MessageField> messageFields = xtext.findAll(MessageField.class);
    verifyError(
        "Tag number 1 conflicts with reserved 1 to 3.",
        messageFields.get(0),
        ProtobufPackage.Literals.MESSAGE_FIELD__INDEX);
    verifyError(
        "Tag number 2 conflicts with reserved 1 to 3.",
        messageFields.get(1),
        ProtobufPackage.Literals.MESSAGE_FIELD__INDEX);
    verifyError(
        "Tag number 3 conflicts with reserved 1 to 3.",
        messageFields.get(2),
        ProtobufPackage.Literals.MESSAGE_FIELD__INDEX);
    verifyNoMoreInteractions(messageAcceptor);
  }
}
