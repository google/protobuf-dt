/*
 * Copyright (c) 2011 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.model.util;

import static com.google.eclipse.protobuf.junit.core.Setups.unitTestSetup;
import static com.google.eclipse.protobuf.junit.core.XtextRule.createWith;
import static org.junit.Assert.*;

import org.junit.*;

import com.google.eclipse.protobuf.junit.core.XtextRule;
import com.google.eclipse.protobuf.protobuf.Property;

/**
 * Tests for <code>{@link Properties#isBool(Property)}</code>.
 *
 * @author alruiz@google.com (Alex Ruiz)
 */
public class Properties_isBool_Test {

  @Rule public XtextRule xtext = createWith(unitTestSetup());

  private Properties properties;

  @Before public void setUp() {
    properties = xtext.getInstanceOf(Properties.class);
  }

  // syntax = "proto2";
  //
  // message Person {
  //   optional bool code = 1;
  // }
  @Test public void should_return_true_if_property_is_bool() {
    Property p = xtext.find("code", Property.class);
    assertTrue(properties.isBool(p));
  }

  // syntax = "proto2";
  //
  // message Person {
  //   optional string name = 1;
  // }
  @Test public void should_return_false_if_property_is_not_bool() {
    Property p = xtext.find("name", Property.class);
    assertFalse(properties.isBool(p));
  }
}
