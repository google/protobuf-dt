/*
 * Copyright (c) 2011 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.conversion;

import static java.lang.Double.NEGATIVE_INFINITY;
import static java.lang.Double.NaN;
import static java.lang.Double.POSITIVE_INFINITY;
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
 * Tests for <code>{@link DOUBLEValueConverter#toValue(String, INode)}</code>.
 *
 * @author alruiz@google.com (Alex Ruiz)
 */
@RunWith(Parameterized.class)
public class DOUBLEValueConverter_toValue_Test {
  @Rule public XtextRule xtext = overrideRuntimeModuleWith(unitTestModule());

  private final String input;
  private final Double expected;

  @Parameters
  public static Collection<Object[]> parameters() {
    return asList(new Object[][] {
      { "52e3", 52e3D },
      { "52E3", 52e3D },
      { "6e-3", 0.006D },
      { "6.8", 6.8D },
      { "-3.1", -3.1D },
      { ".3", 0.3D },
      { "nan", NaN },
      { "inf", POSITIVE_INFINITY },
      { "-inf", NEGATIVE_INFINITY }
    });
  }

  public DOUBLEValueConverter_toValue_Test(String input, Double expected) {
    this.input = input;
    this.expected = expected;
  }

  private DOUBLEValueConverter converter;
  private INode node;

  @Before public void setUp() {
    node = mock(INode.class);
    converter = xtext.injector().getInstance(DOUBLEValueConverter.class);
  }

  @Test public void should_parse_hexadecimal_number() {
    Double value = converter.toValue(input, node);
    assertThat(value, equalTo(expected));
  }
}
