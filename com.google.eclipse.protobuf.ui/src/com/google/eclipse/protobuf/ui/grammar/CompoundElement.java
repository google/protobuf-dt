/*
 * Copyright (c) 2011 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.ui.grammar;

import static com.google.eclipse.protobuf.ui.grammar.CommonKeyword.*;

/**
 * Element composed of one or more keywords.
 *
 * @author alruiz@google.com (Alex Ruiz)
 */
public enum CompoundElement {

  DEFAULT_EQUAL(join(DEFAULT, EQUAL)),
  DEFAULT_EQUAL_IN_BRACKETS(inBrackets(DEFAULT_EQUAL)),
  EMPTY_STRING("\"\""),
  DEFAULT_EQUAL_STRING(join(DEFAULT_EQUAL, EMPTY_STRING)),
  DEFAULT_EQUAL_STRING_IN_BRACKETS(inBrackets(DEFAULT_EQUAL_STRING)),
  PACKED_EQUAL_TRUE(join(PACKED, EQUAL, TRUE)),
  PACKED_EQUAL_TRUE_IN_BRACKETS(inBrackets(PACKED_EQUAL_TRUE));

  private final String value;

  private static String join(CommonKeyword...keywords) {
    StringBuilder buffer = new StringBuilder();
    int count = keywords.length;
    for (int i = 0; i < count; i++) {
      buffer.append(keywords[i].toString());
      if (i < count - 1) buffer.append(" ");
    }
    return buffer.toString();
  }

  private static String join(CompoundElement...elements) {
    StringBuilder buffer = new StringBuilder();
    int count = elements.length;
    for (int i = 0; i < count; i++) {
      buffer.append(elements[i].value);
      if (i < count - 1) buffer.append(" ");
    }
    return buffer.toString();
  }

  private static String inBrackets(CompoundElement element) {
    return String.format("[%s]", element.value);
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
   * Returns the number of characters in this compound element.
   * @return the number of characters in this compound element.
   */
  public int charCount() {
    return value.length();
  }

  /**
   * Returns the index within this compound element of the first occurrence of the value of the specified keyword.
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
