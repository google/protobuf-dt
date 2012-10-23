/*
 * Copyright (c) 2011 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.model.util;

import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;

import static com.google.eclipse.protobuf.junit.core.UnitTestModule.unitTestModule;
import static com.google.eclipse.protobuf.junit.core.XtextRule.overrideRuntimeModuleWith;

import org.junit.Rule;
import org.junit.Test;

import com.google.eclipse.protobuf.junit.core.XtextRule;
import com.google.eclipse.protobuf.protobuf.Literal;
import com.google.inject.Inject;

/**
 * Tests for <code>{@link Literals#calculateNewIndexOf(Literal)}</code>.
 *
 * @author alruiz@google.com (Alex Ruiz)
 */
public class Literals_calculateNewIndexOf_Test {
  @Rule public XtextRule xtext = overrideRuntimeModuleWith(unitTestModule());

  @Inject private Literals literals;

  // syntax = "proto2";
  //
  // enum PhoneType {
  //   MOBILE = 1;
  // }
  @Test public void should_return_zero_for_first_and_only_literal() {
    Literal mobile = xtext.find("MOBILE", Literal.class);
    long index = literals.calculateNewIndexOf(mobile);
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
    long index = literals.calculateNewIndexOf(work);
    assertThat(index, equalTo(6L));
  }
}
