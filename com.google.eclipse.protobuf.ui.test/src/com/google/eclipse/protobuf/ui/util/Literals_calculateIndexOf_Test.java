/*
 * Copyright (c) 2011 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.ui.util;

import static org.eclipse.xtext.EcoreUtil2.getAllContentsOfType;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;

import java.util.List;

import org.junit.*;

import com.google.eclipse.protobuf.junit.XtextRule;
import com.google.eclipse.protobuf.protobuf.*;
import com.google.eclipse.protobuf.protobuf.Enum;

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
    proto.append("enum PhoneType {");
    proto.append("  MOBILE = 0;   ");
    proto.append("}               ");
    Protobuf root = xtext.parse(proto.toString());
    Literal firstLiteral = allLiteralsInFirstEnum(root).get(0);
    int index = literals.calculateIndexOf(firstLiteral);
    assertThat(index, equalTo(0));
  }

  @Test public void should_return_max_index_value_plus_one_for_new_literal() {
    StringBuilder proto = new StringBuilder();
    proto.append("enum PhoneType {");
    proto.append("  MOBILE = 0;   ");
    proto.append("  HOME = 1;     ");
    proto.append("  WORK = 2;     ");
    proto.append("}               ");
    Protobuf root = xtext.parse(proto.toString());
    Literal lastLiteral = allLiteralsInFirstEnum(root).get(2);
    int index = literals.calculateIndexOf(lastLiteral);
    assertThat(index, equalTo(2));
  }

  private List<Literal> allLiteralsInFirstEnum(Protobuf root) {
    List<Enum> allEnums = getAllContentsOfType(root, Enum.class);
    return allEnums.get(0).getLiterals();
  }
}
