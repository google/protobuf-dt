/*
 * Copyright (c) 2011 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.validation;

import static com.google.eclipse.protobuf.validation.Messages.*;
import static org.eclipse.xtext.diagnostics.Diagnostic.SYNTAX_DIAGNOSITC;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.xtext.nodemodel.SyntaxErrorMessage;
import org.eclipse.xtext.parser.antlr.SyntaxErrorMessageProvider;

import com.google.eclipse.protobuf.protobuf.Property;

/**
 * Messages for syntax errors.
 *
 * @author alruiz@google.com (Alex Ruiz)
 */
public class ProtobufSyntaxErrorMessageProvider extends SyntaxErrorMessageProvider {

  @Override public SyntaxErrorMessage getSyntaxErrorMessage(IParserErrorContext context) {
    String message = context.getDefaultMessage();
    EObject currentContext = context.getCurrentContext();
    if (currentContext instanceof Property) message =  mapToProtocMessage(message, (Property) currentContext);
    if (currentContext == null && message.contains("RULE_STRING")) return null;
    return new SyntaxErrorMessage(message, SYNTAX_DIAGNOSITC);
  }

  private String mapToProtocMessage(String message, Property property) {
    if (message.contains("RULE_ID") && property.getName() == null)
      return Error_expectedFieldName;
    if (message.equals("mismatched input ';' expecting '='") && property.getIndex() == 0)
      return Error_missingFieldNumber;
    return message;
  }

}
