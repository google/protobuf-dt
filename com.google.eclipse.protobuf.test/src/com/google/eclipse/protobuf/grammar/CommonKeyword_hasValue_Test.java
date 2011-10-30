/*
 * Copyright (c) 2011 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.grammar;

import static com.google.eclipse.protobuf.grammar.CommonKeyword.*;
import static org.junit.Assert.*;

import org.junit.Test;

/**
 * Tests for <code>{@link CommonKeyword#hasValue(String)}</code>.
 *
 * @author alruiz@google.com (Alex Ruiz)
 */
public class CommonKeyword_hasValue_Test {

  @Test public void should_return_true_if_value_is_equal_to_String() {
    assertTrue(BOOL.hasValue("bool"));
    assertTrue(TRUE.hasValue("true"));
    assertTrue(FALSE.hasValue("false"));
    assertTrue(BYTES.hasValue("bytes"));
    assertTrue(OPENING_BRACKET.hasValue("["));
    assertTrue(CLOSING_BRACKET.hasValue("]"));
    assertTrue(OPENING_CURLY_BRACKET.hasValue("{"));
    assertTrue(CLOSING_CURLY_BRACKET.hasValue("}"));
    assertTrue(DEFAULT.hasValue("default"));
    assertTrue(EQUAL.hasValue("="));
    assertTrue(SEMICOLON.hasValue(";"));
    assertTrue(STRING.hasValue("string"));
  }

  @Test public void should_return_false_if_value_is_not_equal_to_String() {
    assertFalse(STRING.hasValue(";"));
  }
}
