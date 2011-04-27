/*
 * Copyright (c) 2011 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.ui.util;

import static org.hamcrest.core.IsEqual.equalTo;
import static org.hamcrest.core.IsNull.nullValue;
import static org.junit.Assert.assertThat;

import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Tests for <code>{@link Strings#firstCharToLowerCase(String)}</code>
 *
 * @author alruiz@google.com (Alex Ruiz)
 */
public class Strings_firstCharToLowerCase_Test {

  private static Strings strings;
  
  @BeforeClass public static void setUpOnce() {
    strings = new Strings();
  }
  
  @Test public void should_return_null() {
    assertThat(strings.firstCharToLowerCase(null), nullValue());
  }

  @Test public void should_return_empty_String() {
    assertThat(strings.firstCharToLowerCase(""), equalTo(""));
  }
  
  @Test public void should_return_String_with_first_char_lower_case() {
    assertThat(strings.firstCharToLowerCase("HElLo"), equalTo("hElLo"));
  }
}
