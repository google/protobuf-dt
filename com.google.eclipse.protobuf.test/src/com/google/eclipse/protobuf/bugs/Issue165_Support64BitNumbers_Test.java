/*
 * Copyright (c) 2011 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.bugs;

import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;

import static com.google.eclipse.protobuf.junit.core.UnitTestModule.unitTestModule;
import static com.google.eclipse.protobuf.junit.core.XtextRule.overrideRuntimeModuleWith;

import org.eclipse.xtext.nodemodel.INode;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import com.google.eclipse.protobuf.conversion.LONGValueConverter;
import com.google.eclipse.protobuf.junit.core.XtextRule;

/**
 * Tests fix for <a href="http://code.google.com/p/protobuf-dt/issues/detail?id=165">Issue 165</a>.
 *
 * @author alruiz@google.com (Alex Ruiz)
 */
public class Issue165_Support64BitNumbers_Test {
  @Rule public XtextRule xtext = overrideRuntimeModuleWith(unitTestModule());

  private INode node;
  private LONGValueConverter converter;

  @Before public void setUp() {
    node = mock(INode.class);
    converter = xtext.injector().getInstance(LONGValueConverter.class);
  }

  @Test public void should_parse_64_bit_number() {
    Long value = converter.toValue("18446744073709551615", node);
    assertThat(value, equalTo(1L));
  }
}
