/*
 * Copyright (c) 2014 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.validation;

import static com.google.eclipse.protobuf.protobuf.ProtobufPackage.Literals.SYNTAX__NAME;
import static com.google.eclipse.protobuf.validation.ProtobufJavaValidator.SYNTAX_IS_NOT_KNOWN_ERROR;
import static org.eclipse.xtext.validation.ValidationMessageAcceptor.INSIGNIFICANT_INDEX;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

import com.google.eclipse.protobuf.protobuf.Syntax;

import org.eclipse.xtext.validation.ValidationMessageAcceptor;
import org.junit.Before;
import org.junit.Test;

/**
 * Tests for <code>{@link ProtobufJavaValidator#checkSyntaxIsKnown(Syntax)}</code>
 *
 * @author alruiz@google.com (Alex Ruiz)
 */
public class ProtobufJavaValidator_checkSyntaxIsKnown_Test {
  private Syntax syntax;
  private ValidationMessageAcceptor messageAcceptor;
  private ProtobufJavaValidator validator;

  @Before public void setUp() {
    syntax = mock(Syntax.class);
    messageAcceptor = mock(ValidationMessageAcceptor.class);
    validator = new ProtobufJavaValidator();
    validator.setMessageAcceptor(messageAcceptor);
  }

  @Test public void should_create_error_if_syntax_is_not_proto2_or_proto3() {
    when(syntax.getName()).thenReturn("proto5");
    validator.checkSyntaxIsKnown(syntax);
    String message = "Unrecognized syntax identifier \"proto5\".  "
        + "This parser only recognizes \"proto2\" and \"proto3\".";
    verify(messageAcceptor).acceptError(message, syntax, SYNTAX__NAME, INSIGNIFICANT_INDEX,
        SYNTAX_IS_NOT_KNOWN_ERROR);
  }

  @Test public void should_not_create_error_if_syntax_is_proto2() {
    when(syntax.getName()).thenReturn("proto2");
    validator.checkSyntaxIsKnown(syntax);
    verifyZeroInteractions(messageAcceptor);
  }

  @Test public void should_not_create_error_if_syntax_is_proto3() {
    when(syntax.getName()).thenReturn("proto3");
    validator.checkSyntaxIsKnown(syntax);
    verifyZeroInteractions(messageAcceptor);
  }
}
