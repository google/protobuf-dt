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

import static com.google.eclipse.protobuf.junit.core.UnitTestModule.unitTestModule;
import static com.google.eclipse.protobuf.junit.core.XtextRule.overrideRuntimeModuleWith;

import java.util.Collection;

import org.eclipse.xtext.nodemodel.INode;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import com.google.eclipse.protobuf.junit.core.XtextRule;

/**
 * Tests for <code>{@link STRINGValueConverter#toValue(String, INode)}</code>.
 *
 * @author alruiz@google.com (Alex Ruiz)
 */
@RunWith(Parameterized.class)
public class STRINGValueConverter_toValue_Test {
  @Rule public XtextRule xtext = overrideRuntimeModuleWith(unitTestModule());

  private final String input;
  private final String expected;

  @Parameters
  public static Collection<Object[]> parameters() {
    return asList(new Object[][] {
      { null, null },
      { "\"Hello World!\"", "Hello World!" },
      { "\"Hello\"\n\" World!\"", "Hello World!" },
      { "\"Hello\"\r\" World!\"", "Hello World!" },
      { "\"Hello\"\n\n\" World!\"", "Hello World!" },
      { "\"Hello\"\n\r\" World!\"", "Hello World!" }
    });
  }

  public STRINGValueConverter_toValue_Test(String input, String expected) {
    this.input = input;
    this.expected = expected;
  }

  private STRINGValueConverter converter;
  private INode node;

  @Before public void setUp() {
    node = mock(INode.class);
    converter = xtext.injector().getInstance(STRINGValueConverter.class);
  }

  @Test public void should_parse_multi_line_string() {
    String value = converter.toValue(input, node);
    assertThat(value, equalTo(expected));
  }
}
