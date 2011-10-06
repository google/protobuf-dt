/*
 * Copyright (c) 2011 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.model.util;

import static com.google.eclipse.protobuf.junit.core.Setups.unitTestSetup;
import static com.google.eclipse.protobuf.junit.core.XtextRule.createWith;
import static com.google.eclipse.protobuf.junit.model.find.Name.name;
import static com.google.eclipse.protobuf.junit.model.find.PropertyFinder.findProperty;
import static com.google.eclipse.protobuf.junit.model.find.Root.in;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.hamcrest.core.IsNull.nullValue;
import static org.junit.Assert.assertThat;

import com.google.eclipse.protobuf.junit.core.XtextRule;
import com.google.eclipse.protobuf.protobuf.*;
import com.google.eclipse.protobuf.protobuf.Enum;

import org.junit.*;

/**
 * Tests for <code>{@link ModelFinder#enumTypeOf(Property)}</code>.
 *
 * @author alruiz@google.com (Alex Ruiz)
 */
public class ModelFinder_enumTypeOf_Test {

  @Rule public XtextRule xtext = createWith(unitTestSetup());

  private Protobuf root;
  private ModelFinder finder;

  @Before public void setUp() {
    root = xtext.root();
    finder = xtext.getInstanceOf(ModelFinder.class);
  }

  // enum PhoneType {
  //   MOBILE = 0;
  //   HOME = 1;
  //   WORK = 2;
  // }
  //
  // message PhoneNumber {
  //   optional PhoneType type = 1;
  // }
  @Test public void should_return_enum_if_property_type_is_enum() {
    Property type = findProperty(name("type"), in(root));
    Enum phoneType = finder.enumTypeOf(type);
    assertThat(phoneType.getName(), equalTo("PhoneType"));
  }

  // message Person {
  //   optional string name = 1;
  // }
  @Test public void should_return_null_if_property_type_is_not_enum() {
    Property name = findProperty(name("name"), in(root));
    Enum anEnum = finder.enumTypeOf(name);
    assertThat(anEnum, nullValue());
  }
}
