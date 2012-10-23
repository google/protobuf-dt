/*
 * Copyright (c) 2011 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.validation;

import static org.eclipse.xtext.diagnostics.Diagnostic.SYNTAX_DIAGNOSTIC;

import static com.google.eclipse.protobuf.validation.Messages.expectedFieldName;
import static com.google.eclipse.protobuf.validation.Messages.missingFieldNumber;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.xtext.nodemodel.SyntaxErrorMessage;
import org.eclipse.xtext.parser.antlr.SyntaxErrorMessageProvider;

import com.google.eclipse.protobuf.protobuf.MessageField;

/**
 * Messages for syntax errors.
 *
 * @author alruiz@google.com (Alex Ruiz)
 */
public class ProtobufSyntaxErrorMessageProvider extends SyntaxErrorMessageProvider {
  @Override public SyntaxErrorMessage getSyntaxErrorMessage(IParserErrorContext context) {
    String message = context.getDefaultMessage();
    EObject currentContext = context.getCurrentContext();
    if (currentContext instanceof MessageField) {
      message = mapToProtocMessage(message, (MessageField) currentContext);
    }
    if (currentContext == null && message.contains("RULE_STRING")) {
      return null;
    }
    return new SyntaxErrorMessage(message, SYNTAX_DIAGNOSTIC);
  }

  private String mapToProtocMessage(String message, MessageField field) {
    if (message.contains("RULE_ID") && field.getName() == null) {
      return expectedFieldName;
    }
    if (message.equals("mismatched input ';' expecting '='") && field.getIndex() == 0) {
      return missingFieldNumber;
    }
    return message;
  }
}
