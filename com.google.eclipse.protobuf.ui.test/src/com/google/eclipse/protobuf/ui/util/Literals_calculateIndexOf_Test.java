/*
 * Copyright (c) 2011 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.ui.util;

import static com.google.eclipse.protobuf.junit.util.Finder.findLiteral;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;

import org.junit.*;

import com.google.eclipse.protobuf.junit.core.XtextRule;
import com.google.eclipse.protobuf.protobuf.Literal;
import com.google.eclipse.protobuf.protobuf.Protobuf;

/**
 * Tests for <code>{@link Literals#calculateIndexOf(Literal)}</code>.
 *
 * @author alruiz@google.com (Alex Ruiz)
 */
public class Literals_calculateIndexOf_Test {

  @Rule public XtextRule xtext = new XtextRule();

  private Literals literals;

  @Before public void setUp() {
    literals = xtext.getInstanceOf(Literals.class);
  }

  @Test public void should_return_zero_for_first_and_only_literal() {
    StringBuilder proto = new StringBuilder();
    proto.append("enum PhoneType {")
         .append("  MOBILE = 1;   ")
         .append("}               ");
    Protobuf root = xtext.parse(proto);
    Literal mobile = findLiteral("MOBILE", root);
    int index = literals.calculateIndexOf(mobile);
    assertThat(index, equalTo(0));
  }

  @Test public void should_return_max_index_value_plus_one_for_new_literal() {
    StringBuilder proto = new StringBuilder();
    proto.append("enum PhoneType {")
         .append("  MOBILE = 1;   ")
         .append("  HOME = 5;     ")
         .append("  WORK = 9;     ")
         .append("}               ");
    Protobuf root = xtext.parse(proto);
    Literal work = findLiteral("WORK", root);
    int index = literals.calculateIndexOf(work);
    assertThat(index, equalTo(6));
  }
}
