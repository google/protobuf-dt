/*
 * Copyright (c) 2011 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.ui.grammar;

import org.eclipse.xtext.Keyword;

/**
 * A commonly used keyword.
 *
 * @author alruiz@google.com (Alex Ruiz)
 */
public enum CommonKeyword {

  // we used to get keywords from IGrammarAccess. The problem was that we still had to hardcode the keyword we were
  // looking for. The code was too complicated and we had to change our implementation anyway.

  BOOL("bool"), TRUE("true"), FALSE("false"), BYTES("bytes"), OPENING_BRACKET("["), CLOSING_BRACKET("]"),
    DEFAULT("default"), EQUAL("="), PACKED("packet"), SEMICOLON(";"), STRING("string");

  private final String value;

  private CommonKeyword(String value) {
    this.value = value;
  }

  public boolean hasValueEqualTo(Keyword k) {
    return hasValueEqualTo(k.getValue());
  }

  public boolean hasValueEqualTo(String s) {
    return value.equals(s);
  }

  @Override public String toString() {
    return value;
  }
}
