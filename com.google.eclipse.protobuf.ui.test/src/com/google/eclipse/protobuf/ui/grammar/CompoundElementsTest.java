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

import org.junit.*;

import com.google.eclipse.protobuf.junit.XtextRule;

/**
 * Tests for <code>{@link CompoundElements}</code>.
 *
 * @author alruiz@google.com (Alex Ruiz)
 */
public class CompoundElementsTest {

  @Rule public XtextRule xtext = new XtextRule();
  
  private CompoundElements elements;
  
  @Before public void setUp() {
    elements = xtext.getInstanceOf(CompoundElements.class);
  }
  
  @Test public void should_return_default() {
    assertThat(elements.defaultValue(), equalTo("default ="));
  }
  
  @Test public void should_return_default_in_brackets() {
    assertThat(elements.defaultValueInBrackets(), equalTo("[default =]"));
  }
  
  @Test public void should_return_default_for_string() {
    assertThat(elements.defaultStringValue(), equalTo("default = \"\""));
  }
  
  @Test public void should_return_default_for_string_in_brackets() {
    assertThat(elements.defaultStringValueInBrackets(), equalTo("[default = \"\"]"));
  }

  @Test public void should_return_empty_string() {
    assertThat(elements.emptyString(), equalTo("\"\""));
  }
  
  @Test public void should_return_packed() {
    assertThat(elements.packed(), equalTo("packed = true"));
  }
  
  @Test public void should_return_packed_in_brackets() {
    assertThat(elements.packedInBrackets(), equalTo("[packed = true]"));
  }
}
