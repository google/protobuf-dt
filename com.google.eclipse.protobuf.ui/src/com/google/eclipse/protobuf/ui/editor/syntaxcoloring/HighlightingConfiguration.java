/*
 * Copyright (c) 2011 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.ui.editor.syntaxcoloring;

import static com.google.eclipse.protobuf.ui.editor.syntaxcoloring.Messages.*;

import org.eclipse.xtext.ui.editor.syntaxcoloring.*;

/**
 * @author alruiz@google.com (Alex Ruiz)
 */
public class HighlightingConfiguration extends DefaultHighlightingConfiguration {

  @Override public void configure(IHighlightingConfigurationAcceptor acceptor) {
    acceptor.acceptDefaultHighlighting(KEYWORD_ID, keywords, keywordTextStyle());
    acceptor.acceptDefaultHighlighting(PUNCTUATION_ID, punctuationCharacters, punctuationTextStyle());
    acceptor.acceptDefaultHighlighting(COMMENT_ID, comments, commentTextStyle());
    acceptor.acceptDefaultHighlighting(STRING_ID, strings, stringTextStyle());
    acceptor.acceptDefaultHighlighting(NUMBER_ID, numbers, numberTextStyle());
    acceptor.acceptDefaultHighlighting(DEFAULT_ID, defaults, defaultTextStyle());
    acceptor.acceptDefaultHighlighting(INVALID_TOKEN_ID, invalidSymbols, errorTextStyle());
  }

}
