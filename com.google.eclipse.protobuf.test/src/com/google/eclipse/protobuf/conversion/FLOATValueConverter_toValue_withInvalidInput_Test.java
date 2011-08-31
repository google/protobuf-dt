/*
 * Copyright (c) 2011 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.conversion;

import static org.hamcrest.core.IsEqual.equalTo;
import static org.hamcrest.core.IsInstanceOf.instanceOf;
import static org.junit.Assert.*;
import static org.junit.rules.ExpectedException.none;
import static org.mockito.Mockito.mock;

import org.eclipse.xtext.conversion.ValueConverterException;
import org.eclipse.xtext.nodemodel.INode;
import org.junit.*;
import org.junit.rules.ExpectedException;

import com.google.eclipse.protobuf.junit.core.XtextRule;

/**
 * Tests for <code>{@link FLOATValueConverter#toValue(String, INode)}</code>.
 *
 * @author alruiz@google.com (Alex Ruiz)
 */
public class FLOATValueConverter_toValue_withInvalidInput_Test {

  @Rule public XtextRule xtext = new XtextRule();
  @Rule public ExpectedException thrown = none();

  private FLOATValueConverter converter;
  private INode node;

  @Before public void setUp() {
    node = mock(INode.class);
    converter = xtext.injector().getInstance(FLOATValueConverter.class);
  }

  @Test public void should_throw_error_if_input_is_null() {
    thrown.expect(ValueConverterException.class);
    thrown.expectMessage("Couldn't convert empty string to float.");
    converter.toValue(null, node);
  }

  @Test public void should_throw_error_if_input_is_empty() {
    thrown.expect(ValueConverterException.class);
    thrown.expectMessage("Couldn't convert empty string to float.");
    converter.toValue("", node);
  }

  @Test public void should_throw_error_if_conversion_throws_NumberFormatException() {
    try {
      converter.toValue("abc", node);
      fail("Expecting a " + ValueConverterException.class.getName());
    } catch (ValueConverterException e) {
      assertThat(e.getMessage(), equalTo("Couldn't convert 'abc' to float."));
      assertThat(e.getCause(), instanceOf(NumberFormatException.class));
    }
  }
}
