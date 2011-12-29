/*
 * Copyright (c) 2011 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.grammar;

/**
 * A commonly used keyword.
 *
 * @author alruiz@google.com (Alex Ruiz)
 */
public enum CommonKeyword {
  // we used to get keywords from IGrammarAccess. The problem was that we still had to hard-code the keyword we were
  // looking for. The code was too complicated and if the grammar changed for some reason, we had to change our
  // implementation anyway.

  BOOL("bool"), BYTES("bytes"), CLOSING_BRACKET("]"), CLOSING_CURLY_BRACKET("}"), DEFAULT("default"), DOUBLE("double"),
  EQUAL("="), FALSE("false"), FIXED32("fixed32"), FIXED64("fixed64"), FLOAT("float"), INT32("int32"), INT64("int64"),
  NAN("nan"), OPENING_BRACKET("["), OPENING_CURLY_BRACKET("{"), SEMICOLON(";"), SFIXED32("sfixed32"),
  SFIXED64("sfixed64"), STRING("string"), SYNTAX("syntax"), TRUE("true"), SINT32("sint32"), SINT64("sint64"),
  UINT32("unit32"), UINT64("uint64");

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
