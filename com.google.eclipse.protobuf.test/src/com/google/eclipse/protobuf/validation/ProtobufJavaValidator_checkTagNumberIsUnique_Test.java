/*
 * Copyright (c) 2011 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.validation;

import static com.google.eclipse.protobuf.junit.core.Setups.unitTestSetup;
import static com.google.eclipse.protobuf.junit.core.XtextRule.createWith;
import static com.google.eclipse.protobuf.protobuf.ProtobufPackage.Literals.PROPERTY__INDEX;
import static com.google.eclipse.protobuf.validation.ProtobufJavaValidator.INVALID_FIELD_TAG_NUMBER_ERROR;
import static org.eclipse.xtext.validation.ValidationMessageAcceptor.INSIGNIFICANT_INDEX;
import static org.mockito.Mockito.*;

import com.google.eclipse.protobuf.junit.core.XtextRule;
import com.google.eclipse.protobuf.protobuf.*;

import org.eclipse.xtext.validation.ValidationMessageAcceptor;
import org.junit.*;

/**
 * Tests for <code>{@link ProtobufJavaValidator#checkTagNumberIsUnique(IndexedElement)}</code>
 *
 * @author alruiz@google.com (Alex Ruiz)
 */
public class ProtobufJavaValidator_checkTagNumberIsUnique_Test {

  @Rule public XtextRule xtext = createWith(unitTestSetup());

  private ValidationMessageAcceptor messageAcceptor;
  private ProtobufJavaValidator validator;

  @Before public void setUp() {
    messageAcceptor = mock(ValidationMessageAcceptor.class);
    validator = xtext.getInstanceOf(ProtobufJavaValidator.class);
    validator.setMessageAcceptor(messageAcceptor);
  }

  // syntax = "proto2";
  //
  // message Person {
  //   optional long id = 1;
  //   optional string name = 1;
  // }
  @Test public void should_create_error_if_field_does_not_have_unique_tag_number() {
    Property name = xtext.find("name", Property.class);
    validator.checkTagNumberIsUnique(name);
    String message = "Field number 1 has already been used in \"Person\" by field \"id\".";
    verify(messageAcceptor).acceptError(message, name, PROPERTY__INDEX, INSIGNIFICANT_INDEX, INVALID_FIELD_TAG_NUMBER_ERROR);
  }

  // syntax = "proto2";
  //
  // message Person {
  //   optional long id = 1;
  //   optional string name = 2;
  // }
  @Test public void should_not_create_error_if_field_has_unique_tag_number() {
    Property name = xtext.find("name", Property.class);
    validator.checkTagNumberIsUnique(name);
    verifyZeroInteractions(messageAcceptor);
  }
}
