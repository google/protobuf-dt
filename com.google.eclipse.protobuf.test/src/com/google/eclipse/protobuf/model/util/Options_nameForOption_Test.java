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
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;

import static com.google.eclipse.protobuf.junit.core.UnitTestModule.unitTestModule;
import static com.google.eclipse.protobuf.junit.core.XtextRule.overrideRuntimeModuleWith;

import org.junit.Rule;
import org.junit.Test;

import com.google.eclipse.protobuf.junit.core.XtextRule;
import com.google.eclipse.protobuf.protobuf.Group;
import com.google.eclipse.protobuf.protobuf.IndexedElement;
import com.google.eclipse.protobuf.protobuf.MessageField;
import com.google.inject.Inject;

/**
 * Tests for <code>{@link Options#nameForOption(IndexedElement)}</code>
 * @author alruiz@google.com (Alex Ruiz)
 */
public class Options_nameForOption_Test {
  @Rule public XtextRule xtext = overrideRuntimeModuleWith(unitTestModule());

  @Inject private Options options;

  // syntax = "proto2";
  //
  // message Person {
  //  optional String firstName = 6;
  // }
  @Test public void should_return_unchanged_name_if_element_is_Field() {
    MessageField field = xtext.find("firstName", MessageField.class);
    assertThat(options.nameForOption(field), equalTo("firstName"));
  }

  // syntax = "proto2";
  //
  // message Person {
  //  optional group Names = 8 {}
  // }
  @Test public void should_return_name_in_lower_case_if_element_is_Group() {
    Group group = xtext.find("Names", Group.class);
    assertThat(options.nameForOption(group), equalTo("names"));
  }

  @Test public void should_return_null_if_element_is_null() {
    assertNull(options.nameForOption(null));
  }
}
