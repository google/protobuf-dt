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
import com.google.eclipse.protobuf.protobuf.IndexedElement;
import com.google.eclipse.protobuf.protobuf.MessageField;
import com.google.inject.Inject;

/**
 * Tests for <code>{@link IndexedElements#calculateNewIndexFor(IndexedElement)}</code>.
 *
 * @author alruiz@google.com (Alex Ruiz)
 */
public class IndexedElements_calculateNewIndexFor_Test {
  @Rule public XtextRule xtext = overrideRuntimeModuleWith(unitTestModule());

  @Inject private IndexedElements indexedElements;

  // syntax = "proto2";
  //
  // message Person {
  //   required string name = 2;
  // }
  @Test public void should_return_one_for_first_and_only_field() {
    MessageField field = xtext.find("name", MessageField.class);
    long index = indexedElements.calculateNewIndexFor(field);
    assertThat(index, equalTo(3L));
  }

  // syntax = "proto2";
  //
  // message Person {
  //   required string name = 6;
  //   required int32 id = 8;
  // }
  @Test public void should_return_max_index_value_plus_one_for_new_field() {
    MessageField field = xtext.find("id", MessageField.class);
    long index = indexedElements.calculateNewIndexFor(field);
    assertThat(index, equalTo(9L));
  }
}
