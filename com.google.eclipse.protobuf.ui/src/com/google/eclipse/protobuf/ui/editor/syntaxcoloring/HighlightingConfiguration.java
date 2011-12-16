/*
 * Copyright (c) 2011 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License v1.0 which
 * accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.ui.editor.syntaxcoloring;

import static com.google.eclipse.protobuf.ui.editor.syntaxcoloring.Messages.*;

import org.eclipse.xtext.ui.editor.syntaxcoloring.*;
import org.eclipse.xtext.ui.editor.utils.TextStyle;

/**
 * @author alruiz@google.com (Alex Ruiz)
 */
public class HighlightingConfiguration extends DefaultHighlightingConfiguration {

  public static final String ENUM_DEFINITION_ID = "enumDefinition";
  public static final String ENUM_ID = "enum";
  public static final String ENUM_LITERAL_DEFINITION = "enumLiteralDefinition";
  public static final String ENUM_LITERAL_ID = "enumLiteral";
  public static final String ENUM_LITERAL_INDEX_ID = "enumLiteralIndex";
  public static final String MESSAGE_DEFINITION_ID = "messageDefinition";
  public static final String MESSAGE_FIELD_INDEX_ID = "messageFieldIndex";
  public static final String MESSAGE_ID = "message";
  public static final String RPC_ARGUMENT_ID = "rpcArgument";
  public static final String RPC_DEFINITION_ID = "rpcDefinition";
  public static final String RPC_RETURN_TYPE_ID = "rpcReturnType";
  public static final String SERVICE_DEFINITION_ID = "serviceDefinition";

  @Override public void configure(IHighlightingConfigurationAcceptor acceptor) {
    acceptor.acceptDefaultHighlighting(COMMENT_ID, comments, commentTextStyle());
    acceptor.acceptDefaultHighlighting(DEFAULT_ID, defaults, defaultTextStyle());
    acceptor.acceptDefaultHighlighting(ENUM_DEFINITION_ID, enumDefinitions, defaultTextStyle());
    acceptor.acceptDefaultHighlighting(ENUM_ID, enums, defaultTextStyle());
    acceptor.acceptDefaultHighlighting(ENUM_LITERAL_DEFINITION, enumLiteralDefinitions, copyOfDefaultTextStyle());
    acceptor.acceptDefaultHighlighting(ENUM_LITERAL_ID, enumLiterals, copyOfDefaultTextStyle());
    acceptor.acceptDefaultHighlighting(ENUM_LITERAL_INDEX_ID, enumLiteralIndices, copyOfNumberTextStyle());
    acceptor.acceptDefaultHighlighting(INVALID_TOKEN_ID, invalidSymbols, errorTextStyle());
    acceptor.acceptDefaultHighlighting(KEYWORD_ID, keywords, keywordTextStyle());
    acceptor.acceptDefaultHighlighting(MESSAGE_DEFINITION_ID, messageDefinitions, copyOfDefaultTextStyle());
    acceptor.acceptDefaultHighlighting(MESSAGE_FIELD_INDEX_ID, messageFieldIndices, copyOfNumberTextStyle());
    acceptor.acceptDefaultHighlighting(MESSAGE_ID, messages, copyOfDefaultTextStyle());
    acceptor.acceptDefaultHighlighting(NUMBER_ID, numbers, numberTextStyle());
    acceptor.acceptDefaultHighlighting(PUNCTUATION_ID, punctuationCharacters, punctuationTextStyle());
    acceptor.acceptDefaultHighlighting(RPC_ARGUMENT_ID, rpcArguments, copyOfDefaultTextStyle());
    acceptor.acceptDefaultHighlighting(RPC_DEFINITION_ID, rpcDefinitions, copyOfDefaultTextStyle());
    acceptor.acceptDefaultHighlighting(RPC_RETURN_TYPE_ID, rpcReturnTypes, copyOfDefaultTextStyle());
    acceptor.acceptDefaultHighlighting(SERVICE_DEFINITION_ID, serviceDefinitions, copyOfDefaultTextStyle());
    acceptor.acceptDefaultHighlighting(STRING_ID, strings, stringTextStyle());
  }

  private TextStyle copyOfDefaultTextStyle() {
    return defaultTextStyle().copy();
  }

  private TextStyle copyOfNumberTextStyle() {
    return numberTextStyle().copy();
  }
}
