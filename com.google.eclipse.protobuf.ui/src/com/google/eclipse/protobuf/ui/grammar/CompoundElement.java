/*
 * Copyright (c) 2011 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.ui.grammar;

import static com.google.eclipse.protobuf.grammar.CommonKeyword.CLOSING_BRACKET;
import static com.google.eclipse.protobuf.grammar.CommonKeyword.DEFAULT;
import static com.google.eclipse.protobuf.grammar.CommonKeyword.EQUAL;
import static com.google.eclipse.protobuf.grammar.CommonKeyword.OPENING_BRACKET;
import static com.google.eclipse.protobuf.grammar.CommonKeyword.SEMICOLON;
import static com.google.eclipse.protobuf.grammar.Syntaxes.proto2;
import static com.google.eclipse.protobuf.util.CommonWords.space;

import com.google.eclipse.protobuf.grammar.CommonKeyword;

/**
 * Element composed of one or more keywords.
 *
 * @author alruiz@google.com (Alex Ruiz)
 */
public enum CompoundElement {
  DEFAULT_EQUAL(joinWithWhitespace(DEFAULT, EQUAL, "")),
  DEFAULT_EQUAL_IN_BRACKETS(inBrackets(DEFAULT_EQUAL)),
  QUOTE("\""),
  EMPTY_STRING(join(QUOTE, QUOTE)),
  DEFAULT_EQUAL_STRING(joinWithWhitespace(DEFAULT_EQUAL, EMPTY_STRING)),
  DEFAULT_EQUAL_STRING_IN_BRACKETS(inBrackets(DEFAULT_EQUAL_STRING)),
  PROTO2_IN_QUOTES(join(QUOTE, proto2(), QUOTE, SEMICOLON)),
  EQUAL_PROTO2_IN_QUOTES(joinWithWhitespace(EQUAL, PROTO2_IN_QUOTES));

  private final String value;

  private static String join(Object...objects) {
    StringBuilder buffer = new StringBuilder();
    int count = objects.length;
    for (int i = 0; i < count; i++) {
      buffer.append(objects[i].toString());
    }
    return buffer.toString();
  }

  private static String joinWithWhitespace(Object...objects) {
    StringBuilder buffer = new StringBuilder();
    int count = objects.length;
    for (int i = 0; i < count; i++) {
      String s = objects[i].toString();
      buffer.append(s);
      if (i < count - 1 && !s.endsWith(space())) {
        buffer.append(space());
      }
    }
    return buffer.toString();
  }

  private static String inBrackets(CompoundElement element) {
    return OPENING_BRACKET + element.value + CLOSING_BRACKET;
  }

  private CompoundElement(String value) {
    this.value = value;
  }

  /**
   * Indicates whether the value of this compound element is equal to the given {@code String}.
   * @param s the value to compare to.
   * @return {@code true} if the value of this compound element is equal to the given {@code String}, {@code false}
   * otherwise.
   */
  public boolean hasValue(String s) {
    return value.equals(s);
  }

  /**
   * Returns the number of characters in this compound element (similar to calling {@code toString().length()}.)
   * @return the number of characters in this compound element.
   */
  public int charCount() {
    return value.length();
  }

  /**
   * Returns the index within this compound element of the first occurrence of the value of the specified keyword
   * (similar to calling {@code toString().indexOf(keyword.toString())}.
   * @param keyword the given keyword.
   * @return the index within this compound element of the first occurrence of the value of the specified keyword; -1
   * if the value of the given keyword is not found.
   */
  public int indexOf(CommonKeyword keyword) {
    return value.indexOf(keyword.toString());
  }

  /**
   * Returns the textual value of this compound element.
   * @return the textual value of this compound element.
   */
  @Override public String toString() {
    return value;
  }
}
