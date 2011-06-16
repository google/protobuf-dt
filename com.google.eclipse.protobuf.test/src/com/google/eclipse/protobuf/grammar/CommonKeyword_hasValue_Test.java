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
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;

import org.junit.Test;

/**
 * Tests for <code>{@link CommonKeyword#hasValue(String)}</code>.
 *
 * @author alruiz@google.com (Alex Ruiz)
 */
public class CommonKeyword_hasValue_Test {

  @Test public void should_return_true_if_value_is_equal_to_String() {
    assertThat(BOOL.hasValue("bool"), equalTo(true));
    assertThat(TRUE.hasValue("true"), equalTo(true));
    assertThat(FALSE.hasValue("false"), equalTo(true));
    assertThat(BYTES.hasValue("bytes"), equalTo(true));
    assertThat(OPENING_BRACKET.hasValue("["), equalTo(true));
    assertThat(CLOSING_BRACKET.hasValue("]"), equalTo(true));
    assertThat(DEFAULT.hasValue("default"), equalTo(true));
    assertThat(EQUAL.hasValue("="), equalTo(true));
    assertThat(SEMICOLON.hasValue(";"), equalTo(true));
    assertThat(STRING.hasValue("string"), equalTo(true));
  }

  @Test public void should_return_false_if_value_is_not_equal_to_String() {
    assertThat(STRING.hasValue(";"), equalTo(false));
  }

}
