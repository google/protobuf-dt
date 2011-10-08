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
import static com.google.eclipse.protobuf.protobuf.ProtobufPackage.Literals.FIELD__INDEX;
import static com.google.eclipse.protobuf.validation.ProtobufJavaValidator.INVALID_FIELD_TAG_NUMBER_ERROR;
import static org.eclipse.xtext.validation.ValidationMessageAcceptor.INSIGNIFICANT_INDEX;
import static org.mockito.Mockito.*;

import com.google.eclipse.protobuf.junit.core.XtextRule;
import com.google.eclipse.protobuf.protobuf.*;

import org.eclipse.xtext.validation.ValidationMessageAcceptor;
import org.junit.*;

/**
 * Tests for <code>{@link ProtobufJavaValidator#checkTagNumberIsGreaterThanZero(Field)}</code>
 * 
 * @author alruiz@google.com (Alex Ruiz)
 */
public class ProtobufJavaValidator_checkTagNumberIsGreaterThanZero_Test {

  @Rule public XtextRule xtext = createWith(unitTestSetup());
  
  private ValidationMessageAcceptor messageAcceptor;
  private ProtobufJavaValidator validator;
  
  @Before public void setUp() {
    messageAcceptor = mock(ValidationMessageAcceptor.class);
    validator = xtext.getInstanceOf(ProtobufJavaValidator.class);
    validator.setMessageAcceptor(messageAcceptor);
  }

  // message Person {
  //   optional long id = 0;
  // }
  @Test public void should_create_error_if_field_index_is_zero() {
    Property id = xtext.find("id", Property.class);
    validator.checkTagNumberIsGreaterThanZero(id);
    String message = "Field numbers must be positive integers.";
    verify(messageAcceptor).acceptError(message, id, FIELD__INDEX, INSIGNIFICANT_INDEX, INVALID_FIELD_TAG_NUMBER_ERROR);
  }
  
  // message Person {
  //   optional long id = -1;
  // }
  @Test public void should_create_error_if_field_index_is_negative() {
    Property id = xtext.find("id", Property.class);
    validator.checkTagNumberIsGreaterThanZero(id);
    String message = "Expected field number.";
    verify(messageAcceptor).acceptError(message, id, FIELD__INDEX, INSIGNIFICANT_INDEX, INVALID_FIELD_TAG_NUMBER_ERROR);
  }

  // message Person {
  //   optional long id = 1;
  // }
  @Test public void should_not_create_error_if_field_index_is_greater_than_zero() {
    Property id = xtext.find("id", Property.class);
    validator.checkTagNumberIsGreaterThanZero(id);
    verifyZeroInteractions(messageAcceptor);
  }
}
