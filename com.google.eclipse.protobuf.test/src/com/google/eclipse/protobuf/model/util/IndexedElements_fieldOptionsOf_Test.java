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

import java.util.List;

import org.junit.Rule;
import org.junit.Test;

import com.google.eclipse.protobuf.junit.core.XtextRule;
import com.google.eclipse.protobuf.protobuf.FieldOption;
import com.google.eclipse.protobuf.protobuf.Group;
import com.google.eclipse.protobuf.protobuf.IndexedElement;
import com.google.eclipse.protobuf.protobuf.MessageField;
import com.google.inject.Inject;

/**
 * Tests for <code>{@link IndexedElements#fieldOptionsOf(IndexedElement)}</code>
 *
 * @author alruiz@google.com (Alex Ruiz)
 */
public class IndexedElements_fieldOptionsOf_Test {
  @Rule public XtextRule xtext = overrideRuntimeModuleWith(unitTestModule());

  @Inject private IndexedElements indexedElements;

  // syntax = "proto2";
  //
  // message Person {
  //  optional bool active = 1 [default = false, deprecated = true];
  // }
  @Test public void should_return_options_of_MessageField() {
    MessageField field = xtext.find("active", MessageField.class);
    List<FieldOption> fieldOptions = indexedElements.fieldOptionsOf(field);
    assertThat(fieldOptions.size(), equalTo(2));
  }

  // syntax = "proto2";
  //
  // message Person {
  //  optional group Names = 8 [deprecated = true] {}
  // }
  @Test public void should_return_index_of_Group() {
    Group group = xtext.find("Names", Group.class);
    List<FieldOption> fieldOptions = indexedElements.fieldOptionsOf(group);
    assertThat(fieldOptions.size(), equalTo(1));
  }
}
