/*
 * Copyright (c) 2011 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.ui.util;

import static com.google.eclipse.protobuf.junit.core.UnitTestModule.unitTestModule;
import static com.google.eclipse.protobuf.junit.core.XtextRule.overrideRuntimeModuleWith;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;

import org.junit.*;

import com.google.eclipse.protobuf.junit.core.XtextRule;
import com.google.eclipse.protobuf.protobuf.Literal;

/**
 * Tests for <code>{@link Literals#calculateIndexOf(Literal)}</code>.
 *
 * @author alruiz@google.com (Alex Ruiz)
 */
public class Literals_calculateIndexOf_Test {
  @Rule public XtextRule xtext = overrideRuntimeModuleWith(unitTestModule());

  private Literals literals;

  @Before public void setUp() {
    literals = xtext.getInstanceOf(Literals.class);
  }

  // syntax = "proto2";
  //
  // enum PhoneType {
  //   MOBILE = 1;
  // }
  @Test public void should_return_zero_for_first_and_only_literal() {
    Literal mobile = xtext.find("MOBILE", Literal.class);
    long index = literals.calculateIndexOf(mobile);
    assertThat(index, equalTo(0L));
  }

  // syntax = "proto2";
  //
  // enum PhoneType {
  //   MOBILE = 1;
  //   HOME = 5;
  //   WORK = 9;
  // }
  @Test public void should_return_max_index_value_plus_one_for_new_literal() {
    Literal work = xtext.find("WORK", Literal.class);
    long index = literals.calculateIndexOf(work);
    assertThat(index, equalTo(6L));
  }
}
