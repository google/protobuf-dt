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
import static com.google.eclipse.protobuf.protobuf.ProtobufPackage.Literals.MAP_TYPE__KEY_TYPE;
import static com.google.eclipse.protobuf.validation.Messages.invalidMapKeyType;
import static com.google.eclipse.protobuf.validation.ProtobufJavaValidator.INVALID_MAP_KEY_TYPE_ERROR;
import static org.eclipse.xtext.validation.ValidationMessageAcceptor.INSIGNIFICANT_INDEX;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;

import com.google.eclipse.protobuf.junit.core.XtextRule;
import com.google.eclipse.protobuf.protobuf.MapType;
import com.google.eclipse.protobuf.protobuf.MapTypeLink;
import com.google.eclipse.protobuf.protobuf.MessageField;
import com.google.inject.Inject;

import org.eclipse.xtext.validation.ValidationMessageAcceptor;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

/**
 * Tests for <code>{@link ProtobufJavaValidator#checkMapTypeHasValidKeyType(MapType)}</code>
 */
public class ProtobufJavaValidator_checkMapTypeHasValidKeyType_Test {
  @Rule public XtextRule xtext = overrideRuntimeModuleWith(unitTestModule());

  @Inject private ProtobufJavaValidator validator;
  private ValidationMessageAcceptor messageAcceptor;

  @Before public void setUp() {
    messageAcceptor = mock(ValidationMessageAcceptor.class);
    validator.setMessageAcceptor(messageAcceptor);
  }

  // syntax = "proto2";
  //
  // message Bar {}
  //
  // message Foo {
  //   map<Bar, string> bar = 1;
  // }
  @Test public void should_create_error_if_key_type_is_message() {
    MessageField field = xtext.find("bar", MessageField.class);
    MapType map = ((MapTypeLink) field.getType()).getTarget();
    validator.checkMapTypeHasValidKeyType(map);
    verify(messageAcceptor).acceptError(invalidMapKeyType, map, MAP_TYPE__KEY_TYPE,
        INSIGNIFICANT_INDEX, INVALID_MAP_KEY_TYPE_ERROR);
  }

  // syntax = "proto2";
  //
  // message Foo {
  //   map<double, string> bar = 1;
  // }
  @Test public void should_create_error_if_key_type_is_double() {
    MessageField field = xtext.find("bar", MessageField.class);
    MapType map = ((MapTypeLink) field.getType()).getTarget();
    validator.checkMapTypeHasValidKeyType(map);
    verify(messageAcceptor).acceptError(invalidMapKeyType, map, MAP_TYPE__KEY_TYPE,
        INSIGNIFICANT_INDEX, INVALID_MAP_KEY_TYPE_ERROR);
  }

  // syntax = "proto2";
  //
  // message Foo {
  //   map<float, string> bar = 1;
  // }
  @Test public void should_create_error_if_key_type_is_float() {
    MessageField field = xtext.find("bar", MessageField.class);
    MapType map = ((MapTypeLink) field.getType()).getTarget();
    validator.checkMapTypeHasValidKeyType(map);
    verify(messageAcceptor).acceptError(invalidMapKeyType, map, MAP_TYPE__KEY_TYPE,
        INSIGNIFICANT_INDEX, INVALID_MAP_KEY_TYPE_ERROR);
  }

  // syntax = "proto2";
  //
  // message Foo {
  //   map<bytes, string> bar = 1;
  // }
  @Test public void should_create_error_if_key_type_is_bytes() {
    MessageField field = xtext.find("bar", MessageField.class);
    MapType map = ((MapTypeLink) field.getType()).getTarget();
    validator.checkMapTypeHasValidKeyType(map);
    verify(messageAcceptor).acceptError(invalidMapKeyType, map, MAP_TYPE__KEY_TYPE,
        INSIGNIFICANT_INDEX, INVALID_MAP_KEY_TYPE_ERROR);
  }

  // syntax = "proto2";
  //
  // message Foo {
  //   map<map<string, string>, string> bar = 1;
  // }
  @Test public void should_create_error_if_key_type_is_map() {
    MessageField field = xtext.find("bar", MessageField.class);
    MapType map = ((MapTypeLink) field.getType()).getTarget();
    validator.checkMapTypeHasValidKeyType(map);
    verify(messageAcceptor).acceptError(invalidMapKeyType, map, MAP_TYPE__KEY_TYPE,
        INSIGNIFICANT_INDEX, INVALID_MAP_KEY_TYPE_ERROR);
  }

  // syntax = "proto2";
  //
  // message Foo {
  //   map<bool, string> bar = 1;
  // }
  @Test public void should_not_create_error_if_key_type_is_bool() {
    MessageField field = xtext.find("bar", MessageField.class);
    MapType map = ((MapTypeLink) field.getType()).getTarget();
    validator.checkMapTypeHasValidKeyType(map);
    verifyZeroInteractions(messageAcceptor);
  }

  // syntax = "proto2";
  //
  // message Foo {
  //   map<int32, string> bar = 1;
  // }
  @Test public void should_not_create_error_if_key_type_is_scalar() {
    MessageField field = xtext.find("bar", MessageField.class);
    MapType map = ((MapTypeLink) field.getType()).getTarget();
    validator.checkMapTypeHasValidKeyType(map);
    verifyZeroInteractions(messageAcceptor);
  }

  // syntax = "proto2";
  //
  // message Foo {
  //   map<string, string> bar = 1;
  // }
  @Test public void should_not_create_error_if_key_type_is_string() {
    MessageField field = xtext.find("bar", MessageField.class);
    MapType map = ((MapTypeLink) field.getType()).getTarget();
    validator.checkMapTypeHasValidKeyType(map);
    verifyZeroInteractions(messageAcceptor);
  }
}
