/*
 * Copyright (c) 2014 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.validation;

import static com.google.eclipse.protobuf.junit.core.UnitTestModule.unitTestModule;
import static com.google.eclipse.protobuf.junit.core.XtextRule.overrideRuntimeModuleWith;
import static com.google.eclipse.protobuf.protobuf.ProtobufPackage.Literals.MESSAGE_FIELD__MODIFIER;
import static com.google.eclipse.protobuf.validation.Messages.mapWithModifier;
import static com.google.eclipse.protobuf.validation.Messages.missingModifier;
import static com.google.eclipse.protobuf.validation.Messages.oneofFieldWithModifier;
import static com.google.eclipse.protobuf.validation.ProtobufJavaValidator.MAP_WITH_MODIFIER_ERROR;
import static com.google.eclipse.protobuf.validation.ProtobufJavaValidator.MISSING_MODIFIER_ERROR;
import static com.google.eclipse.protobuf.validation.ProtobufJavaValidator.ONEOF_FIELD_WITH_MODIFIER_ERROR;
import static org.eclipse.xtext.validation.ValidationMessageAcceptor.INSIGNIFICANT_INDEX;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;

import com.google.eclipse.protobuf.junit.core.XtextRule;
import com.google.eclipse.protobuf.protobuf.MessageField;
import com.google.inject.Inject;

import org.eclipse.xtext.validation.ValidationMessageAcceptor;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

/**
 * Tests for <code>{@link ProtobufJavaValidator#checkFieldModifiers(MessageField)}</code>
 */
public class ProtobufJavaValidator_checkFieldModifiers_Test {
  @Rule public XtextRule xtext = overrideRuntimeModuleWith(unitTestModule());

  @Inject private ProtobufJavaValidator validator;
  private ValidationMessageAcceptor messageAcceptor;

  @Before public void setUp() {
    messageAcceptor = mock(ValidationMessageAcceptor.class);
    validator.setMessageAcceptor(messageAcceptor);
  }

  // syntax = "proto2";
  //
  // message Foo {
  //   string bar = 1;
  // }
  @Test public void should_create_error_if_no_modifier_in_proto2() {
    MessageField field = xtext.find("bar", MessageField.class);
    validator.checkFieldModifiers(field);
    verify(messageAcceptor).acceptError(missingModifier, field, MESSAGE_FIELD__MODIFIER,
        INSIGNIFICANT_INDEX, MISSING_MODIFIER_ERROR);
  }

  // syntax = "proto3";
  //
  // message Foo {
  //   string bar = 1;
  // }
  @Test public void should_not_create_error_if_no_modifier_in_proto3() {
    MessageField field = xtext.find("bar", MessageField.class);
    validator.checkFieldModifiers(field);
    verifyZeroInteractions(messageAcceptor);
  }

  // syntax = "proto3";
  //
  // message Foo {
  //   required string bar = 1;
  // }
  @Test public void should_create_error_if_required_modifier_in_proto3() {
    MessageField field = xtext.find("bar", MessageField.class);
    validator.checkFieldModifiers(field);
    verify(messageAcceptor).acceptError(Messages.requiredInProto3, field, MESSAGE_FIELD__MODIFIER,
        INSIGNIFICANT_INDEX, ProtobufJavaValidator.REQUIRED_IN_PROTO3_ERROR);
  }

  // syntax = "proto2";
  //
  // message Foo {
  //   optional map<string, string> bar = 1;
  // }
  @Test public void should_create_error_if_modifier_on_map_in_proto2() {
    MessageField field = xtext.find("bar", MessageField.class);
    validator.checkFieldModifiers(field);
    verify(messageAcceptor).acceptError(mapWithModifier, field, MESSAGE_FIELD__MODIFIER,
        INSIGNIFICANT_INDEX, MAP_WITH_MODIFIER_ERROR);
  }

  // syntax = "proto3";
  //
  // message Foo {
  //   optional map<string, string> bar = 1;
  // }
  @Test public void should_create_error_if_modifier_on_map_in_proto3() {
    MessageField field = xtext.find("bar", MessageField.class);
    validator.checkFieldModifiers(field);
    verify(messageAcceptor).acceptError(mapWithModifier, field, MESSAGE_FIELD__MODIFIER,
        INSIGNIFICANT_INDEX, MAP_WITH_MODIFIER_ERROR);
  }

  // syntax = "proto2";
  //
  // message Foo {
  //   map<string, string> bar = 1;
  // }
  @Test public void should_not_create_error_if_no_modifier_on_map_in_proto2() {
    MessageField field = xtext.find("bar", MessageField.class);
    validator.checkFieldModifiers(field);
    verifyZeroInteractions(messageAcceptor);
  }

  // syntax = "proto2";
  //
  // message Foo {
  //   oneof test {
  //     string bar = 1;
  //     string baz = 2;
  //   }
  // }
  @Test public void should_not_create_error_if_no_modifier_on_oneof_field() {
    MessageField field = xtext.find("bar", MessageField.class);
    validator.checkFieldModifiers(field);
    verifyZeroInteractions(messageAcceptor);
  }

  // syntax = "proto2";
  //
  // message Foo {
  //   oneof test {
  //     repeated string bar = 1;
  //     string baz = 2;
  //   }
  // }
  @Test public void should_create_error_if_modifier_on_oneof_field() {
    MessageField field = xtext.find("bar", MessageField.class);
    validator.checkFieldModifiers(field);
    verify(messageAcceptor).acceptError(oneofFieldWithModifier, field, MESSAGE_FIELD__MODIFIER,
        INSIGNIFICANT_INDEX, ONEOF_FIELD_WITH_MODIFIER_ERROR);
  }
}
