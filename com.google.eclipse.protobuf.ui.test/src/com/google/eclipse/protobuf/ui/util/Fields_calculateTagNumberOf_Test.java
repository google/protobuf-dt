/*
 * Copyright (c) 2011 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.ui.util;

import static com.google.eclipse.protobuf.junit.model.find.Name.name;
import static com.google.eclipse.protobuf.junit.model.find.PropertyFinder.findProperty;
import static com.google.eclipse.protobuf.junit.model.find.Root.in;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;

import org.junit.*;

import com.google.eclipse.protobuf.junit.core.XtextRule;
import com.google.eclipse.protobuf.junit.util.MultiLineTextBuilder;
import com.google.eclipse.protobuf.protobuf.*;

/**
 * Tests for <code>{@link Fields#calculateTagNumberOf(Field)}</code>.
 *
 * @author alruiz@google.com (Alex Ruiz)
 */
public class Fields_calculateTagNumberOf_Test {

  @Rule public XtextRule xtext = XtextRule.unitTestSetup();

  private Fields fields;

  @Before public void setUp() {
    fields = xtext.getInstanceOf(Fields.class);
  }

  @Test public void should_return_one_for_first_and_only_property() {
    MultiLineTextBuilder proto = new MultiLineTextBuilder();
    proto.append("message Person {           ")
         .append("  required string name = 2;")
         .append("}                          ");
    Protobuf root = xtext.parseText(proto);
    Property name = findProperty(name("name"), in(root));
    long index = fields.calculateTagNumberOf(name);
    assertThat(index, equalTo(1L));
  }

  @Test public void should_return_max_tag_number_value_plus_one_for_new_property() {
    MultiLineTextBuilder proto = new MultiLineTextBuilder();
    proto.append("message Person {           ")
         .append("  required string name = 6;")
         .append("  required int32 id = 8;   ")
         .append("}                          ");
    Protobuf root = xtext.parseText(proto);
    Property id = findProperty(name("id"), in(root));
    long index = fields.calculateTagNumberOf(id);
    assertThat(index, equalTo(7L));
  }
}
