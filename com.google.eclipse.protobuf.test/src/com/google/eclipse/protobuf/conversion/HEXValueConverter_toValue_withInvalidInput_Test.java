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
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;
import static org.junit.rules.ExpectedException.none;
import static org.mockito.Mockito.mock;

import static com.google.eclipse.protobuf.junit.core.UnitTestModule.unitTestModule;
import static com.google.eclipse.protobuf.junit.core.XtextRule.overrideRuntimeModuleWith;

import org.eclipse.xtext.conversion.ValueConverterException;
import org.eclipse.xtext.nodemodel.INode;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import com.google.eclipse.protobuf.junit.core.XtextRule;

/**
 * Tests for <code>{@link HEXValueConverter#toValue(String, INode)}</code>.
 *
 * @author alruiz@google.com (Alex Ruiz)
 */
public class HEXValueConverter_toValue_withInvalidInput_Test {
  @Rule public XtextRule xtext = overrideRuntimeModuleWith(unitTestModule());
  @Rule public ExpectedException thrown = none();

  private HEXValueConverter converter;
  private INode node;

  @Before public void setUp() {
    node = mock(INode.class);
    converter = xtext.injector().getInstance(HEXValueConverter.class);
  }

  @Test public void should_throw_error_if_input_is_null() {
    thrown.expect(ValueConverterException.class);
    thrown.expectMessage("Couldn't convert empty string to long.");
    converter.toValue(null, node);
  }

  @Test public void should_throw_error_if_input_is_empty() {
    thrown.expect(ValueConverterException.class);
    thrown.expectMessage("Couldn't convert empty string to long.");
    converter.toValue("", node);
  }

  @Test public void should_throw_error_if_input_has_less_than_three_characters() {
    thrown.expect(ValueConverterException.class);
    thrown.expectMessage("Couldn't convert '0x' to long.");
    converter.toValue("0x", node);
  }

  @Test public void should_throw_error_if_conversion_throws_NumberFormatException() {
    try {
      converter.toValue("0xZ", node);
      fail("Expecting a " + ValueConverterException.class.getName());
    } catch (ValueConverterException e) {
      assertThat(e.getMessage(), equalTo("Couldn't convert '0xZ' to long."));
      assertThat(e.getCause(), instanceOf(NumberFormatException.class));
    }
  }
}
