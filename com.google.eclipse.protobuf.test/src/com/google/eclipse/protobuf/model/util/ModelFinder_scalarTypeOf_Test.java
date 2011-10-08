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
import static org.hamcrest.core.IsEqual.equalTo;
import static org.hamcrest.core.IsNull.nullValue;
import static org.junit.Assert.assertThat;

import com.google.eclipse.protobuf.junit.core.XtextRule;
import com.google.eclipse.protobuf.protobuf.*;

import org.junit.*;

/**
 * Tests for <code>{@link ModelFinder#scalarTypeOf(Property)}</code>.
 *
 * @author alruiz@google.com (Alex Ruiz)
 */
public class ModelFinder_scalarTypeOf_Test {

  @Rule public XtextRule xtext = createWith(unitTestSetup());

  private ModelFinder finder;

  @Before public void setUp() {
    finder = xtext.getInstanceOf(ModelFinder.class);
  }

  // message Person {
  //   optional int32 id = 1;
  // }
  @Test public void should_return_scalar_if_property_type_is_scalar() {
    Property id = xtext.find("id", Property.class);
    ScalarType int32 = finder.scalarTypeOf(id);
    assertThat(int32.getName(), equalTo("int32"));
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
  @Test public void should_return_null_if_property_type_is_not_scalar() {
    Property type = xtext.find("type", Property.class);
    ScalarType scalar = finder.scalarTypeOf(type);
    assertThat(scalar, nullValue());
  }
}
