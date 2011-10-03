/*
 * Copyright (c) 2011 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.model.util;

import static com.google.eclipse.protobuf.junit.model.find.Name.name;
import static com.google.eclipse.protobuf.junit.model.find.PropertyFinder.findProperty;
import static com.google.eclipse.protobuf.junit.model.find.Root.in;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;

import org.junit.*;

import com.google.eclipse.protobuf.junit.core.XtextRule;
import com.google.eclipse.protobuf.junit.util.MultiLineTextBuilder;
import com.google.eclipse.protobuf.model.util.Properties;
import com.google.eclipse.protobuf.protobuf.*;

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
    Protobuf root = xtext.parseText(proto);
    Property name = findProperty(name("name"), in(root));
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
    Protobuf root = xtext.parseText(proto);
    Property number = findProperty(name("number"), in(root));
    assertThat(properties.typeNameOf(number), equalTo("PhoneNumber"));
  }

}
