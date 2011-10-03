/*
 * Copyright (c) 2011 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.util;

import static com.google.eclipse.protobuf.junit.find.Name.name;
import static com.google.eclipse.protobuf.junit.find.OptionFinder.findOption;
import static com.google.eclipse.protobuf.junit.find.Root.in;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;

import org.junit.*;

import com.google.eclipse.protobuf.junit.core.XtextRule;
import com.google.eclipse.protobuf.junit.util.MultiLineTextBuilder;
import com.google.eclipse.protobuf.protobuf.*;

/**
 * Tests for <code>{@link Options#propertyFrom(Option)}</code>.
 *
 * @author alruiz@google.com (Alex Ruiz)
 */
public class Options_propertyFrom_Test {

  @Rule public XtextRule xtext = XtextRule.integrationTestSetup();

  private Options options;

  @Before public void setUp() {
    options = xtext.getInstanceOf(Options.class);
  }

  @Test public void should_return_property_of_native_option() {
    MultiLineTextBuilder proto = new MultiLineTextBuilder();
    proto.append("option java_package = 'com.google.eclipse.protobuf.tests';");
    Protobuf root = xtext.parseText(proto);
    Option option = findOption(name("java_package"), in(root));
    Property p = options.propertyFrom(option);
    assertThat(p.getName(), equalTo("java_package"));
  }

  @Test public void should_return_property_of_custom_option() {
    MultiLineTextBuilder proto = new MultiLineTextBuilder();
    proto.append("import 'google/protobuf/descriptor.proto';")
         .append("                                          ")
         .append("extend google.protobuf.FileOptions {      ")
         .append("  optional string encoding = 1000;        ")
         .append("}                                         ")
         .append("                                          ")
         .append("option (encoding) = 'UTF-8';              ");
    Protobuf root = xtext.parseText(proto);
    Option option = findOption(name("encoding"), in(root));
    Property p = options.propertyFrom(option);
    assertThat(p.getName(), equalTo("encoding"));
  }
}
