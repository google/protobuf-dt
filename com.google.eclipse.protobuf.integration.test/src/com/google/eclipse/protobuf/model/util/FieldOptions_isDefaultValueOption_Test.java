/*
 * Copyright (c) 2011 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.model.util;

import static com.google.eclipse.protobuf.junit.find.FieldOptionFinder.findFieldOption;
import static com.google.eclipse.protobuf.junit.find.Name.name;
import static com.google.eclipse.protobuf.junit.find.Root.in;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;

import org.junit.*;

import com.google.eclipse.protobuf.junit.core.XtextRule;
import com.google.eclipse.protobuf.junit.util.MultiLineTextBuilder;
import com.google.eclipse.protobuf.model.util.FieldOptions;
import com.google.eclipse.protobuf.protobuf.*;

/**
 * Tests for <code>{@link FieldOptions#isDefaultValueOption(FieldOption)}</code>.
 *
 * @author alruiz@google.com (Alex Ruiz)
 */
public class FieldOptions_isDefaultValueOption_Test {

  @Rule public XtextRule xtext = XtextRule.integrationTestSetup();

  private FieldOptions fieldOptions;
  private Protobuf root;

  @Before public void setUp() {
    fieldOptions = xtext.getInstanceOf(FieldOptions.class);
    MultiLineTextBuilder proto = new MultiLineTextBuilder();
    proto.append("message Person {                                                   ")
         .append("  optional boolean active = 1 [default = true, deprecated = false];")
         .append("}                                                                  ");
    root = xtext.parseText(proto);
  }

  @Test public void should_return_true_if_FieldOption_is_default_value_one() {
    FieldOption option = findFieldOption(name("default"), in(root));
    boolean result = fieldOptions.isDefaultValueOption(option);
    assertThat(result, equalTo(true));
  }

  @Test public void should_return_false_if_FieldOption_is_not_default_value_one() {
    FieldOption option = findFieldOption(name("deprecated"), in(root));
    boolean result = fieldOptions.isDefaultValueOption(option);
    assertThat(result, equalTo(false));
  }
}
