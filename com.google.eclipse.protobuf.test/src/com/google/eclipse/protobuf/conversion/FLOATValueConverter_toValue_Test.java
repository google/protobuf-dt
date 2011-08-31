/*
 * Copyright (c) 2011 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.conversion;

import static java.util.Arrays.asList;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;

import java.util.Collection;

import org.eclipse.xtext.nodemodel.INode;
import org.junit.*;
import org.junit.runner.RunWith;
import org.junit.runners.*;
import org.junit.runners.Parameterized.Parameters;

import com.google.eclipse.protobuf.junit.core.XtextRule;

/**
 * Tests for <code>{@link FLOATValueConverter#toValue(String, INode)}</code>.
 *
 * @author alruiz@google.com (Alex Ruiz)
 */
@RunWith(Parameterized.class)
public class FLOATValueConverter_toValue_Test {

  @Rule public XtextRule xtext = new XtextRule();

  private final String input;
  private final Float expected;

  @Parameters
  public static Collection<Object[]> parameters() {
    return asList(new Object[][] {
      { "52e3", 52e3F },
      { "52E3", 52e3F },
      { "6e-3", 0.006F },
      { "6.8", 6.8F },
      { "-3.1", -3.1F },
      { ".3", 0.3F }
    });
  }

  public FLOATValueConverter_toValue_Test(String input, Float expected) {
    this.input = input;
    this.expected = expected;
  }

  private FLOATValueConverter converter;
  private INode node;

  @Before public void setUp() {
    node = mock(INode.class);
    converter = xtext.injector().getInstance(FLOATValueConverter.class);
  }

  @Test public void should_parse_hexadecimal_number() {
    Float value = converter.toValue(input, node);
    assertThat(value, equalTo(expected));
  }
}
