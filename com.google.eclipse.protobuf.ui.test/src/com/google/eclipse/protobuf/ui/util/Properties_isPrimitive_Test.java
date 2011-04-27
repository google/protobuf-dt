/*
 * Copyright (c) 2011 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.ui.util;

import static org.eclipse.xtext.EcoreUtil2.getAllContentsOfType;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;

import java.util.List;

import org.junit.*;

import com.google.eclipse.protobuf.junit.XtextRule;
import com.google.eclipse.protobuf.protobuf.Property;
import com.google.eclipse.protobuf.protobuf.Protobuf;

/**
 * Tests for <code>{@link Properties#isPrimitive(Property)}</code>.
 *
 * @author alruiz@google.com (Alex Ruiz)
 */
public class Properties_isPrimitive_Test {

  @Rule public XtextRule xtext = new XtextRule();

  private Properties properties;

  @Before public void setUp() {
    properties = xtext.getInstanceOf(Properties.class);
  }

  @Test public void should_return_true_if_property_is_primitive() {
    StringBuilder proto = new StringBuilder();
    proto.append("message Primitives {             ")
         .append("  optional float float_1 = 1;    ")
         .append("  optional int32 int32_1 = 2;    ")
         .append("  optional int64 int64_1 = 3;    ")
         .append("  optional uint32 uint32_1 = 4;  ")
         .append("  optional uint64 uint64_1 = 5;  ")
         .append("  optional sint32 sint32_1 = 6;  ")
         .append("  optional sint64 sint64_1 = 7;  ")
         .append("  optional fixed32 fixed32_1 = 8;")
         .append("  optional fixed64 fixed64_1 = 9;")
         .append("  optional bool bool_1 = 10;     ")
         .append("}                                ");
    Protobuf root = xtext.parse(proto);
    List<Property> allProperties = getAllContentsOfType(root, Property.class);
    for (Property p : allProperties)
      assertThat(properties.isPrimitive(p), equalTo(true));
  }

  @Test public void should_return_false_if_property_is_not_primitive() {
    StringBuilder proto = new StringBuilder();
    proto.append("message Types {                  ")
         .append("  optional string string_1 = 1;  ")
         .append("  optional bytes bytes_1 = 2;    ")
         .append("  optional Person person = 3;    ")
         .append("}                                ")
         .append("                                 ")
         .append("message Person {                 ")
         .append("  optional string name = 1       ")
         .append("}                                ");
    Protobuf root = xtext.parse(proto);
    List<Property> allProperties = getAllContentsOfType(root, Property.class);
    for (Property p : allProperties)
      assertThat(properties.isPrimitive(p), equalTo(false));
  }
}
