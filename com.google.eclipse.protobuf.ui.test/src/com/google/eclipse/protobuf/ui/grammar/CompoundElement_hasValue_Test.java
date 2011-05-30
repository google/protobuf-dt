/*
 * Copyright (c) 2011 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.ui.grammar;

import static com.google.eclipse.protobuf.ui.grammar.CompoundElement.*;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;

import org.junit.Test;

/**
 * Tests for <code>{@link CompoundElement#hasValue(String)}</code>.
 *
 * @author alruiz@google.com (Alex Ruiz)
 */
public class CompoundElement_hasValue_Test {

  @Test public void should_return_true_if_value_is_equal_to_String() {
    assertThat(DEFAULT_EQUAL.hasValue("default = "), equalTo(true));
    assertThat(DEFAULT_EQUAL_IN_BRACKETS.hasValue("[default = ]"), equalTo(true));
    assertThat(EMPTY_STRING.hasValue("\"\""), equalTo(true));
    assertThat(DEFAULT_EQUAL_STRING.hasValue("default = \"\""), equalTo(true));
    assertThat(DEFAULT_EQUAL_STRING_IN_BRACKETS.hasValue("[default = \"\"]"), equalTo(true));
  }

  @Test public void should_return_false_if_value_is_not_equal_to_String() {
    assertThat(DEFAULT_EQUAL.hasValue("packed ="), equalTo(false));
  }
}
