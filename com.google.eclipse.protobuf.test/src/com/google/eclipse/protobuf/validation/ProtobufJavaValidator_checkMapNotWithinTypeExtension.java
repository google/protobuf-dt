/*
 * Copyright (c) 2015 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.validation;

import static com.google.eclipse.protobuf.junit.core.UnitTestModule.unitTestModule;
import static com.google.eclipse.protobuf.junit.core.XtextRule.overrideRuntimeModuleWith;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
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
 * Tests for <code>{@link ProtobufJavaValidator#checkMapNotWithinTypeExtension(MapType)}</code>
 */
public class ProtobufJavaValidator_checkMapNotWithinTypeExtension {
  @Rule public XtextRule xtext = overrideRuntimeModuleWith(unitTestModule());

  @Inject private ProtobufJavaValidator validator;
  private ValidationMessageAcceptor messageAcceptor;

  @Before public void setUp() {
    messageAcceptor = mock(ValidationMessageAcceptor.class);
    validator.setMessageAcceptor(messageAcceptor);
  }

  // syntax = "proto2";
  //
  // extend proto2.FieldOptions {
  //   map<string, string> bar = 1;
  // }
  @Test public void should_create_error_if_map_within_type_extension() {
    MessageField field = xtext.find("bar", MessageField.class);
    MapType map = ((MapTypeLink) field.getType()).getTarget();
    validator.checkMapIsNotWithinExtension(map);
    verify(messageAcceptor).acceptError(
        Messages.mapWithinTypeExtension,
        map,
        null,
        ValidationMessageAcceptor.INSIGNIFICANT_INDEX,
        null);
  }

  // syntax = "proto2";
  //
  // extend proto2.FieldOptions {
  //   optional group my_group = 1 {
  //     map<string, string> bar = 2;
  //   }
  // }
  @Test public void should_create_error_if_map_within_group_within_type_extension() {
    MessageField field = xtext.find("bar", MessageField.class);
    MapType map = ((MapTypeLink) field.getType()).getTarget();
    validator.checkMapIsNotWithinExtension(map);
    verify(messageAcceptor).acceptError(
        Messages.mapWithinTypeExtension,
        map,
        null,
        ValidationMessageAcceptor.INSIGNIFICANT_INDEX,
        null);
  }
}
