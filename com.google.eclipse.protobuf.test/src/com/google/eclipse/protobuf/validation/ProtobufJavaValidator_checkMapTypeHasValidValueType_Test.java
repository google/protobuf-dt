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
import static com.google.eclipse.protobuf.protobuf.ProtobufPackage.Literals.MAP_TYPE__VALUE_TYPE;
import static com.google.eclipse.protobuf.validation.Messages.invalidMapValueType;
import static com.google.eclipse.protobuf.validation.ProtobufJavaValidator.MAP_WITH_MAP_VALUE_TYPE_ERROR;
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
 * Tests for <code>{@link ProtobufJavaValidator#checkMapTypeHasValidValueType(MapType)}</code>
 */
public class ProtobufJavaValidator_checkMapTypeHasValidValueType_Test {
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
  //   map<string, map<string, string> > bar = 1;
  // }
  @Test public void should_create_error_if_value_type_is_map() {
    MessageField field = xtext.find("bar", MessageField.class);
    MapType map = ((MapTypeLink) field.getType()).getTarget();
    validator.checkMapTypeHasValidValueType(map);
    verify(messageAcceptor).acceptError(invalidMapValueType, map, MAP_TYPE__VALUE_TYPE,
        INSIGNIFICANT_INDEX, MAP_WITH_MAP_VALUE_TYPE_ERROR);
  }

  // syntax = "proto2";
  //
  // message Bar {}
  //
  // message Foo {
  //   map<string, Bar> bar = 1;
  // }
  @Test public void should_not_create_error_if_value_type_is_message() {
    MessageField field = xtext.find("bar", MessageField.class);
    MapType map = ((MapTypeLink) field.getType()).getTarget();
    validator.checkMapTypeHasValidValueType(map);
    verifyZeroInteractions(messageAcceptor);
  }
}
