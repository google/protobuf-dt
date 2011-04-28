/*
 * Copyright (c) 2011 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.ui.grammar;

import static com.google.eclipse.protobuf.junit.KeywordHasValueMatcher.hasValue;
import static org.junit.Assert.assertThat;

import org.junit.*;

import com.google.eclipse.protobuf.junit.XtextRule;

/**
 * Tests for <code>{@link Keywords}</code>.
 *
 * @author alruiz@google.com (Alex Ruiz)
 */
public class KeywordsTest {

  @Rule public XtextRule xtext = new XtextRule();

  private Keywords keywords;
  
  @Before public void setUp() {
    keywords = xtext.getInstanceOf(Keywords.class);
  }
  
  @Test public void should_return_bool() {
    assertThat(keywords.bool(), hasValue("bool"));
  }

  @Test public void should_return_true() {
    assertThat(keywords.boolTrue(), hasValue("true"));
  }

  @Test public void should_return_false() {
    assertThat(keywords.boolFalse(), hasValue("false"));
  }
  
  @Test public void should_return_bytes() {
    assertThat(keywords.bytes(), hasValue("bytes"));
  }
  
  @Test public void should_return_opening_bracket() {
    assertThat(keywords.openingBracket(), hasValue("["));
  }
  
  @Test public void should_return_closing_bracket() {
    assertThat(keywords.closingBracket(), hasValue("]"));
  }

  @Test public void should_return_default() {
    assertThat(keywords.defaultValue(), hasValue("default"));
  }
  
  @Test public void should_return_equal_sign() {
    assertThat(keywords.equalSign(), hasValue("="));
  }

  @Test public void should_return_packed() {
    assertThat(keywords.packed(), hasValue("packed"));
  }

  @Test public void should_return_semicolon() {
    assertThat(keywords.semicolon(), hasValue(";"));
  }

  @Test public void should_return_string() {
    assertThat(keywords.string(), hasValue("string"));
  }
}
