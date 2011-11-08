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
import static org.eclipse.xtext.EcoreUtil2.getAllContentsOfType;
import static org.junit.Assert.*;

import org.junit.*;

import com.google.eclipse.protobuf.junit.core.XtextRule;
import com.google.eclipse.protobuf.protobuf.Property;

/**
 * Tests for <code>{@link Properties#isPrimitive(Property)}</code>.
 *
 * @author alruiz@google.com (Alex Ruiz)
 */
public class Properties_isPrimitive_Test {

  @Rule public XtextRule xtext = createWith(unitTestSetup());

  private Properties properties;

  @Before public void setUp() {
    properties = xtext.getInstanceOf(Properties.class);
  }

  // syntax = "proto2";
  //
  // message Primitives {
  //   optional float float_1 = 1;
  //   optional int32 int32_1 = 2;
  //   optional int64 int64_1 = 3;
  //   optional uint32 uint32_1 = 4;
  //   optional uint64 uint64_1 = 5;
  //   optional sint32 sint32_1 = 6;
  //   optional sint64 sint64_1 = 7;
  //   optional fixed32 fixed32_1 = 8;
  //   optional fixed64 fixed64_1 = 9;
  //   optional bool bool_1 = 10;
  // }
  @Test public void should_return_true_if_property_is_primitive() {
    for (Property p : getAllContentsOfType(xtext.root(), Property.class))
      assertTrue(properties.isPrimitive(p));
  }

  // syntax = "proto2";
  //
  // message Types {
  //   optional string string_1 = 1;
  //   optional bytes bytes_1 = 2;
  //   optional Person person = 3;
  // }
  //
  // message Person {
  //   optional string name = 1;
  // }
  @Test public void should_return_false_if_property_is_not_primitive() {
    for (Property p : getAllContentsOfType(xtext.root(), Property.class))
      assertFalse(properties.isPrimitive(p));
  }
}
