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
 * Tests for <code>{@link Properties#typeNameOf(Property)}</code>.
 *
 * @author alruiz@google.com (Alex Ruiz)
 */
public class Properties_typeNameOf_Test {

  @Rule public XtextRule xtext = XtextRule.unitTestSetup();

  private Properties properties;

  @Before public void setUp() {
    properties = xtext.getInstanceOf(Properties.class);
  }

  @Test public void should_return_name_of_scalar() {
    MultiLineTextBuilder proto = new MultiLineTextBuilder();
    proto.append("message Person {           ")
         .append("  optional string name = 1;")
         .append("}                          ");
    Protobuf root = xtext.parse(proto);
    Property name = findProperty("name", root);
    assertThat(properties.typeNameOf(name), equalTo("string"));
  }

  @Test public void should_return_name_of_type() {
    MultiLineTextBuilder proto = new MultiLineTextBuilder();
    proto.append("message Person {                  ")
         .append("  optional string name = 1;       ")
         .append("  optional PhoneNumber number = 2;")
         .append("                                  ")
         .append("  message PhoneNumber {           ")
         .append("    optional string value = 1;    ")
         .append("  }                               ")
         .append("}                                 ");
    Protobuf root = xtext.parse(proto);
    Property number = findProperty("number", root);
    assertThat(properties.typeNameOf(number), equalTo("PhoneNumber"));
  }

}
