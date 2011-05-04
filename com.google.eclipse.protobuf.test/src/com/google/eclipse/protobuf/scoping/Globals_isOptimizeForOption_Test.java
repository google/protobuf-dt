/*
 * Copyright (c) 2011 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.scoping;

import static com.google.eclipse.protobuf.junit.util.Finder.findOption;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;

import org.junit.*;

import com.google.eclipse.protobuf.junit.core.XtextRule;
import com.google.eclipse.protobuf.protobuf.Option;
import com.google.eclipse.protobuf.protobuf.Protobuf;

/**
 * Tests for <code>{@link Globals#isOptimizeForOption(Option)}</code>.
 *
 * @author alruiz@google.com (Alex Ruiz)
 */
public class Globals_isOptimizeForOption_Test {

  @Rule public XtextRule xtext = new XtextRule();
  
  private Globals globals;
  
  @Before public void setUp() {
    globals = xtext.getInstanceOf(Globals.class);
  }
  
  @Test public void should_return_true_if_option_is_OptimizeForOption() {
    StringBuilder proto = new StringBuilder();
    proto.append("option java_generate_equals_and_hash = false;")
         .append("option optimize_for = CODE_SIZE;             "); 
    Protobuf root = xtext.parse(proto);
    Option option = findOption("optimize_for", root);
    assertThat(globals.isOptimizeForOption(option), equalTo(true));
  }

  @Test public void should_return_false_if_option_is_not_OptimizeForOption() {
    StringBuilder proto = new StringBuilder();
    proto.append("option java_generate_equals_and_hash = false;")
         .append("option optimize_for = CODE_SIZE;             "); 
    Protobuf root = xtext.parse(proto);
    Option option = findOption("java_generate_equals_and_hash", root);
    assertThat(globals.isOptimizeForOption(option), equalTo(false));
  }

  @Test public void should_return_false_if_option_is_null() {
    assertThat(globals.isOptimizeForOption(null), equalTo(false));
  }
}
