/*
 * Copyright (c) 2011 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.util;

import static com.google.eclipse.protobuf.junit.util.Finder.findProperty;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;

import org.junit.*;

import com.google.eclipse.protobuf.junit.core.XtextRule;
import com.google.eclipse.protobuf.junit.util.MultiLineTextBuilder;
import com.google.eclipse.protobuf.protobuf.Property;
import com.google.eclipse.protobuf.protobuf.Protobuf;

/**
 * Tests for <code>{@link Properties#isBool(Property)}</code>.
 *
 * @author alruiz@google.com (Alex Ruiz)
 */
public class Properties_isBool_Test {

  @Rule public XtextRule xtext = new XtextRule();

  private Properties properties;

  @Before public void setUp() {
    properties = xtext.getInstanceOf(Properties.class);
  }

  @Test public void should_return_true_if_property_is_bool() {
    MultiLineTextBuilder proto = new MultiLineTextBuilder();
    proto.append("message Person {           ")
         .append("  optional bool active = 1;")
         .append("}                          ");
    Protobuf root = xtext.parse(proto);
    Property active = findProperty("active", root);
    assertThat(properties.isBool(active), equalTo(true));
  }

  @Test public void should_return_false_if_property_is_not_bool() {
    MultiLineTextBuilder proto = new MultiLineTextBuilder();
    proto.append("message Person {           ")
         .append("  optional string name = 1;")
         .append("}                          ");
    Protobuf root = xtext.parse(proto);
    Property name = findProperty("name", root);
    assertThat(properties.isBool(name), equalTo(false));
  }
}
