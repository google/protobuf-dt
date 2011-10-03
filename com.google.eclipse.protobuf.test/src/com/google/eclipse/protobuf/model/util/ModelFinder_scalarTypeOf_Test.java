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
import static org.hamcrest.core.IsNull.nullValue;
import static org.junit.Assert.assertThat;

import org.junit.*;

import com.google.eclipse.protobuf.junit.core.XtextRule;
import com.google.eclipse.protobuf.junit.util.MultiLineTextBuilder;
import com.google.eclipse.protobuf.model.util.ModelFinder;
import com.google.eclipse.protobuf.protobuf.*;

/**
 * Tests for <code>{@link ModelFinder#scalarTypeOf(Property)}</code>.
 *
 * @author alruiz@google.com (Alex Ruiz)
 */
public class ModelFinder_scalarTypeOf_Test {

  @Rule public XtextRule xtext = XtextRule.unitTestSetup();

  private ModelFinder finder;

  @Before public void setUp() {
    finder = xtext.getInstanceOf(ModelFinder.class);
  }

  @Test public void should_return_scalar_if_property_type_is_scalar() {
    MultiLineTextBuilder proto = new MultiLineTextBuilder();
    proto.append("message Person {        ")
         .append("  optional int32 id = 1;")
         .append("}                       ");
    Protobuf root = xtext.parseText(proto);
    Property id = findProperty(name("id"), in(root));
    ScalarType int32 = finder.scalarTypeOf(id);
    assertThat(int32.getName(), equalTo("int32"));
  }

  @Test public void should_return_null_if_property_type_is_not_scalar() {
    MultiLineTextBuilder proto = new MultiLineTextBuilder();
    proto.append("enum PhoneType {              ")
         .append("  MOBILE = 0;                 ")
         .append("  HOME = 1;                   ")
         .append("  WORK = 2;                   ")
         .append("}                             ")
         .append("                              ")
         .append("message PhoneNumber {         ")
         .append("  optional PhoneType type = 1;")
         .append("}                             ");
    Protobuf root = xtext.parseText(proto);
    Property type = findProperty(name("type"), in(root));
    ScalarType scalar = finder.scalarTypeOf(type);
    assertThat(scalar, nullValue());
  }
}
