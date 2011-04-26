/*
 * Copyright (c) 2011 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.ui.util;

import static com.google.eclipse.protobuf.ui.util.ProtobufElements.findProperty;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;

import org.junit.*;

import com.google.eclipse.protobuf.junit.XtextRule;
import com.google.eclipse.protobuf.protobuf.Property;
import com.google.eclipse.protobuf.protobuf.Protobuf;

/**
 * Tests for <code>{@link Properties#isString(Property)}</code>.
 *
 * @author alruiz@google.com (Alex Ruiz)
 */
public class Properties_isString_Test {

  @Rule public XtextRule xtext = new XtextRule();

  private Properties properties;

  @Before public void setUp() {
    properties = xtext.getInstanceOf(Properties.class);
  }

  @Test public void should_return_true_if_property_is_string() {
    StringBuilder proto = new StringBuilder();
    proto.append("message Person {           ")
         .append("  optional string name = 1;")
         .append("}                          ");
    Protobuf root = xtext.parse(proto.toString());
    Property name = findProperty("name", root);
    assertThat(properties.isString(name), equalTo(true));
  }

  @Test public void should_return_false_if_property_is_not_string() {
    StringBuilder proto = new StringBuilder();
    proto.append("message Person {           ")
         .append("  optional bool active = 1;")
         .append("}                          ");
    Protobuf root = xtext.parse(proto.toString());
    Property active = findProperty("active", root);
    assertThat(properties.isString(active), equalTo(false));
  }
}
