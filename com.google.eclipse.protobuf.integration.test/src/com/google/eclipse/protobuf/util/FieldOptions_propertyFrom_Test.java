/*
 * Copyright (c) 2011 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.util;

import static com.google.eclipse.protobuf.junit.find.FieldOptionFinder.findFieldOption;
import static com.google.eclipse.protobuf.junit.find.Name.name;
import static com.google.eclipse.protobuf.junit.find.Root.in;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;

import org.junit.*;

import com.google.eclipse.protobuf.junit.core.XtextRule;
import com.google.eclipse.protobuf.junit.util.MultiLineTextBuilder;
import com.google.eclipse.protobuf.protobuf.*;

/**
 * Tests for <code>{@link FieldOptions#propertyFrom(FieldOption)}</code>.
 *
 * @author alruiz@google.com (Alex Ruiz)
 */
public class FieldOptions_propertyFrom_Test {

  @Rule public XtextRule xtext = XtextRule.integrationTestSetup();

  private FieldOptions fieldOptions;

  @Before public void setUp() {
    fieldOptions = xtext.getInstanceOf(FieldOptions.class);
  }

  @Test public void should_return_property_of_native_field_option() {
    MultiLineTextBuilder proto = new MultiLineTextBuilder();
    proto.append("message Person {                                   ")
         .append("  optional boolean active = 1 [deprecated = false];")
         .append("}                                                  ");
    Protobuf root = xtext.parseText(proto);
    FieldOption option = findFieldOption(name("deprecated"), in(root));
    Property p = fieldOptions.propertyFrom(option);
    assertThat(p.getName(), equalTo("deprecated"));
  }

  @Test public void should_return_property_of_custom_field_option() {
    MultiLineTextBuilder proto = new MultiLineTextBuilder();
    proto.append("import \"google/protobuf/descriptor.proto\";         ")
         .append("                                                     ")
         .append("extend google.protobuf.FieldOptions {                ")
         .append("  optional string encoding = 1000;                   ")
         .append("}                                                    ")
         .append("                                                     ")
         .append("message Person {                                     ")
         .append("  optional boolean active = 1 [(encoding) = 'UTF-8'];")
         .append("}                                                    ");
    Protobuf root = xtext.parseText(proto);
    FieldOption option = findFieldOption(name("encoding"), in(root));
    Property p = fieldOptions.propertyFrom(option);
    assertThat(p.getName(), equalTo("encoding"));
  }
}
