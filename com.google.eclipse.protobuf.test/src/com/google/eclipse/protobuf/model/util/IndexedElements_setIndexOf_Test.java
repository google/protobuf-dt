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

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import com.google.eclipse.protobuf.junit.core.XtextRule;
import com.google.eclipse.protobuf.protobuf.Group;
import com.google.eclipse.protobuf.protobuf.IndexedElement;
import com.google.eclipse.protobuf.protobuf.MessageField;

/**
 * Tests for <code>{@link IndexedElements#setIndexTo(IndexedElement, long)}</code>
 *
 * @author alruiz@google.com (Alex Ruiz)
 */
public class IndexedElements_setIndexOf_Test {
  @Rule public XtextRule xtext = overrideRuntimeModuleWith(unitTestModule());

  private IndexedElements indexedElements;

  @Before public void setUp() {
    indexedElements = new IndexedElements();
  }

  // syntax = "proto2";
  //
  // message Person {
  //  optional String firstName = 6;
  // }
  @Test public void should_set_index_of_MessageField() {
    MessageField field = xtext.find("firstName", MessageField.class);
    indexedElements.setIndexTo(field, 1L);
    assertThat(field.getIndex(), equalTo(1L));
  }

  // syntax = "proto2";
  //
  // message Person {
  //  optional group Names = 8 {}
  // }
  @Test public void should_set_index_of_Group() {
    Group group = xtext.find("Names", Group.class);
    indexedElements.setIndexTo(group, 1L);
    assertThat(group.getIndex(), equalTo(1L));
  }
}
