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
import com.google.eclipse.protobuf.protobuf.Group;
import com.google.eclipse.protobuf.protobuf.IndexedElement;
import com.google.eclipse.protobuf.protobuf.MessageField;
import com.google.inject.Inject;

/**
 * Tests for <code>{@link IndexedElements#indexOf(IndexedElement)}</code>
 *
 * @author alruiz@google.com (Alex Ruiz)
 */
public class IndexedElements_indexOf_Test {
  @Rule public XtextRule xtext = overrideRuntimeModuleWith(unitTestModule());

  @Inject private IndexedElements indexedElements;

  // syntax = "proto2";
  //
  // message Person {
  //  optional String firstName = 6;
  // }
  @Test public void should_return_index_of_MessageField() {
    MessageField field = xtext.find("firstName", MessageField.class);
    long index = indexedElements.indexOf(field);
    assertThat(index, equalTo(6L));
  }

  // syntax = "proto2";
  //
  // message Person {
  //  optional group Names = 8 {}
  // }
  @Test public void should_return_index_of_Group() {
    Group group = xtext.find("Names", Group.class);
    long index = indexedElements.indexOf(group);
    assertThat(index, equalTo(8L));
  }

  @Test public void should_return_MIN_VALUE_if_IndexedElement_is_null() {
    assertThat(indexedElements.indexOf(null), equalTo(Long.MIN_VALUE));
  }
}
