/*
 * Copyright (c) 2011 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.model.util;

import static com.google.eclipse.protobuf.junit.find.FieldOptionFinder.findFieldOption;
import static com.google.eclipse.protobuf.junit.find.Name.name;
import static com.google.eclipse.protobuf.junit.find.Root.in;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import com.google.eclipse.protobuf.junit.core.XtextRule;
import com.google.eclipse.protobuf.junit.util.MultiLineTextBuilder;
import com.google.eclipse.protobuf.model.util.FieldOptions;
import com.google.eclipse.protobuf.protobuf.CustomFieldOption;
import com.google.eclipse.protobuf.protobuf.Property;
import com.google.eclipse.protobuf.protobuf.Protobuf;

/**
 * Tests for <code>{@link FieldOptions#fieldFrom(CustomFieldOption)}</code>.
 * 
 * alruiz@google.com (Alex Ruiz)
 */
public class FieldOptions_fieldFrom_Test {

  @Rule public XtextRule xtext = XtextRule.integrationTestSetup();

  private FieldOptions fieldOptions;

  @Before public void setUp() {
    fieldOptions = xtext.getInstanceOf(FieldOptions.class);
  }
  
  @Test public void should_return_property_field() {
    MultiLineTextBuilder proto = new MultiLineTextBuilder();
    proto.append("import 'google/protobuf/descriptor.proto';         ")
         .append("                                                   ")
         .append("message Custom {                                   ")
         .append("  optional int32 count = 1;                        ")
         .append("}                                                  ")
         .append("                                                   ")
         .append("extend google.protobuf.FieldOptions {              ")
         .append("  optional Custom custom = 1000;                   ")
         .append("}                                                  ")
         .append("                                                   ")
         .append("message Person {                                   ")
         .append("  optional boolean active = 1 [(custom).count = 6];")
         .append("}                                                  ");
    Protobuf root = xtext.parseText(proto);
    CustomFieldOption option = (CustomFieldOption) findFieldOption(name("custom"), in(root));
    Property p = fieldOptions.fieldFrom(option);
    assertThat(p.getName(), equalTo("count"));
  }
}
