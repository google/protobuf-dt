/*
 * Copyright (c) 2011 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.ui.util;

import static com.google.eclipse.protobuf.junit.core.Setups.unitTestSetup;
import static com.google.eclipse.protobuf.junit.core.XtextRule.createWith;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;

import org.junit.*;

import com.google.eclipse.protobuf.junit.core.XtextRule;
import com.google.eclipse.protobuf.protobuf.*;

/**
 * Tests for <code>{@link Fields#calculateTagNumberOf(Field)}</code>.
 *
 * @author alruiz@google.com (Alex Ruiz)
 */
public class Fields_calculateTagNumberOf_Test {

  @Rule public XtextRule xtext = createWith(unitTestSetup());

  private Fields fields;

  @Before public void setUp() {
    fields = xtext.getInstanceOf(Fields.class);
  }

  // syntax = "proto2";
  //
  // message Person {
  //   required string name = 2;
  // }
  @Test public void should_return_one_for_first_and_only_property() {
    Property name = xtext.find("name", Property.class);
    long index = fields.calculateTagNumberOf(name);
    assertThat(index, equalTo(1L));
  }

  // syntax = "proto2";
  //
  // message Person {
  //   required string name = 6;
  //   required int32 id = 8;
  // }
  @Test public void should_return_max_tag_number_value_plus_one_for_new_property() {
    Property id = xtext.find("id", Property.class);
    long index = fields.calculateTagNumberOf(id);
    assertThat(index, equalTo(7L));
  }
}
