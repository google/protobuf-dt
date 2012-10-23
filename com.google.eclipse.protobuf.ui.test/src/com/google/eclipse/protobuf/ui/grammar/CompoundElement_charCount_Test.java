/*
 * Copyright (c) 2011 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.ui.grammar;

import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;

import static com.google.eclipse.protobuf.ui.grammar.CompoundElement.DEFAULT_EQUAL;
import static com.google.eclipse.protobuf.ui.grammar.CompoundElement.DEFAULT_EQUAL_IN_BRACKETS;

import org.junit.Test;

/**
 * Tests for <code>{@link CompoundElement#charCount()}</code>.
 *
 * @author alruiz@google.com (Alex Ruiz)
 */
public class CompoundElement_charCount_Test {
  @Test public void should_return_number_of_characters_in_value() {
    assertThat(DEFAULT_EQUAL.charCount(), equalTo(DEFAULT_EQUAL.toString().length()));
    assertThat(DEFAULT_EQUAL_IN_BRACKETS.charCount(), equalTo(DEFAULT_EQUAL_IN_BRACKETS.toString().length()));
  }
}
