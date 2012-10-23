/*
 * Copyright (c) 2011 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.ui.grammar;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import static com.google.eclipse.protobuf.ui.grammar.CompoundElement.DEFAULT_EQUAL;
import static com.google.eclipse.protobuf.ui.grammar.CompoundElement.DEFAULT_EQUAL_IN_BRACKETS;
import static com.google.eclipse.protobuf.ui.grammar.CompoundElement.DEFAULT_EQUAL_STRING;
import static com.google.eclipse.protobuf.ui.grammar.CompoundElement.DEFAULT_EQUAL_STRING_IN_BRACKETS;
import static com.google.eclipse.protobuf.ui.grammar.CompoundElement.EMPTY_STRING;

import org.junit.Test;

/**
 * Tests for <code>{@link CompoundElement#hasValue(String)}</code>.
 *
 * @author alruiz@google.com (Alex Ruiz)
 */
public class CompoundElement_hasValue_Test {
  @Test public void should_return_true_if_value_is_equal_to_String() {
    assertTrue(DEFAULT_EQUAL.hasValue("default = "));
    assertTrue(DEFAULT_EQUAL_IN_BRACKETS.hasValue("[default = ]"));
    assertTrue(EMPTY_STRING.hasValue("\"\""));
    assertTrue(DEFAULT_EQUAL_STRING.hasValue("default = \"\""));
    assertTrue(DEFAULT_EQUAL_STRING_IN_BRACKETS.hasValue("[default = \"\"]"));
  }

  @Test public void should_return_false_if_value_is_not_equal_to_String() {
    assertFalse(DEFAULT_EQUAL.hasValue("packed ="));
  }
}
