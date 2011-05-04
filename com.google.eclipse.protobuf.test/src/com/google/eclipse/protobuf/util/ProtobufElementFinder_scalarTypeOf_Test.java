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
import static org.hamcrest.core.IsNull.nullValue;
import static org.junit.Assert.assertThat;

import org.junit.*;

import com.google.eclipse.protobuf.junit.core.XtextRule;
import com.google.eclipse.protobuf.protobuf.*;

/**
 * Tests for <code>{@link ProtobufElementFinder#scalarTypeOf(Property)}</code>.
 *
 * @author alruiz@google.com (Alex Ruiz)
 */
public class ProtobufElementFinder_scalarTypeOf_Test {

  @Rule public XtextRule xtext = new XtextRule();

  private ProtobufElementFinder finder;

  @Before public void setUp() {
    finder = xtext.getInstanceOf(ProtobufElementFinder.class);
  }

  @Test public void should_return_scalar_if_property_type_is_scalar() {
    StringBuilder proto = new StringBuilder();
    proto.append("message Person {        ")
         .append("  optional int32 id = 1;")
         .append("}                       ");
    Protobuf root = xtext.parse(proto);
    Property id = findProperty("id", root);
    ScalarType int32 = finder.scalarTypeOf(id);
    assertThat(int32.getName(), equalTo("int32"));
  }

  @Test public void should_return_null_if_property_type_is_not_scalar() {
    StringBuilder proto = new StringBuilder();
    proto.append("enum PhoneType {              ")
         .append("  MOBILE = 0;                 ")
         .append("  HOME = 1;                   ")
         .append("  WORK = 2;                   ")
         .append("}                             ")
         .append("                              ")
         .append("message PhoneNumber {         ")
         .append("  optional PhoneType type = 1;")
         .append("}                             ");
    Protobuf root = xtext.parse(proto);
    Property type = findProperty("type", root);
    ScalarType scalar = finder.scalarTypeOf(type);
    assertThat(scalar, nullValue());
  }
}
