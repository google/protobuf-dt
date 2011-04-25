/*
 * Copyright (c) 2011 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.ui.util;

import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;

import org.junit.Before;
import org.junit.Test;

import com.google.eclipse.protobuf.protobuf.Literal;
import com.google.eclipse.protobuf.stubs.EnumStub;
import com.google.eclipse.protobuf.stubs.LiteralStub;

/**
 * Tests for <code>{@link Literals#calculateIndexOf(Literal)}</code>.
 *
 * @author alruiz@google.com (Alex Ruiz)
 */
public class Literals_calculateIndexOf_Test {

  private EnumStub anEnum;

  private Literals literals;
  
  @Before public void setUp() {
    anEnum = new EnumStub("PhoneType");
    literals = new Literals();
  }
  
  @Test public void should_return_zero_for_first_literal() {
    LiteralStub literal = new LiteralStub("HOME");
    anEnum.add(literal);
    int index = literals.calculateIndexOf(literal);
    assertThat(index, equalTo(0));
  }
  
  @Test public void should_return_max_index_value_plus_one_for_new_literal() {
    int maxIndexValue = 1;
    LiteralStub literal1 = new LiteralStub("HOME");
    literal1.setIndex(maxIndexValue);
    LiteralStub literal2 = new LiteralStub("WORK");
    anEnum.add(literal1, literal2);
    int index = literals.calculateIndexOf(literal2);
    assertThat(index, equalTo(maxIndexValue + 1));
  }
}
