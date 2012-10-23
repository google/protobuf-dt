/*
 * Copyright (c) 2011 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.validation;

import static org.eclipse.xtext.validation.ValidationMessageAcceptor.INSIGNIFICANT_INDEX;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

import static com.google.eclipse.protobuf.protobuf.ProtobufPackage.Literals.SYNTAX__NAME;
import static com.google.eclipse.protobuf.validation.ProtobufJavaValidator.SYNTAX_IS_NOT_PROTO2_ERROR;

import org.eclipse.xtext.validation.ValidationMessageAcceptor;
import org.junit.Before;
import org.junit.Test;

import com.google.eclipse.protobuf.protobuf.Syntax;

/**
 * Tests for <code>{@link ProtobufJavaValidator#checkSyntaxIsProto2(Syntax)}</code>
 *
 * @author alruiz@google.com (Alex Ruiz)
 */
public class ProtobufJavaValidator_checkSyntaxIsProto2_Test {
  private Syntax syntax;
  private ValidationMessageAcceptor messageAcceptor;
  private ProtobufJavaValidator validator;

  @Before public void setUp() {
    syntax = mock(Syntax.class);
    messageAcceptor = mock(ValidationMessageAcceptor.class);
    validator = new ProtobufJavaValidator();
    validator.setMessageAcceptor(messageAcceptor);
  }

  @Test public void should_create_error_if_syntax_is_not_proto2() {
    when(syntax.getName()).thenReturn("proto1");
    validator.checkSyntaxIsProto2(syntax);
    String message = "Unrecognized syntax identifier \"proto1\".  This parser only recognizes \"proto2\".";
    verify(messageAcceptor).acceptError(message, syntax, SYNTAX__NAME, INSIGNIFICANT_INDEX, SYNTAX_IS_NOT_PROTO2_ERROR);
  }

  @Test public void should_not_create_error_if_syntax_is_proto2() {
    when(syntax.getName()).thenReturn("proto2");
    validator.checkSyntaxIsProto2(syntax);
    verifyZeroInteractions(messageAcceptor);
  }
}
