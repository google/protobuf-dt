/*
 * Copyright (c) 2011 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.ui.grammar;

/**
 * A commonly used keyword.
 *
 * @author alruiz@google.com (Alex Ruiz)
 */
public enum CommonKeyword {

  // we used to get keywords from IGrammarAccess. The problem was that we still had to hardcode the keyword we were
  // looking for. The code was too complicated and if the grammar changed for some reason, we had to change our
  // implementation anyway.

  BOOL("bool"), TRUE("true"), FALSE("false"), BYTES("bytes"), OPENING_BRACKET("["), CLOSING_BRACKET("]"),
    DEFAULT("default"), EQUAL("="), SEMICOLON(";"), STRING("string"), SYNTAX("syntax");

  private final String value;

  private CommonKeyword(String value) {
    this.value = value;
  }

  /**
   * Indicates whether the value of this keyword is equal to the given {@code String}.
   * @param s the value to compare to.
   * @return {@code true} if the value of this keyword is equal to the given {@code String}, {@code false} otherwise.
   */
  public boolean hasValue(String s) {
    return value.equals(s);
  }

  /**
   * Returns the textual value of this keyword.
   * @return the textual value of this keyword.
   */
  @Override public String toString() {
    return value;
  }
}
