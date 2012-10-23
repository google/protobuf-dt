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
import static com.google.eclipse.protobuf.protobuf.ProtobufPackage.Literals.GROUP__INDEX;
import static com.google.eclipse.protobuf.protobuf.ProtobufPackage.Literals.MESSAGE_FIELD__INDEX;

import org.eclipse.emf.ecore.EStructuralFeature;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;

import com.google.eclipse.protobuf.junit.core.XtextRule;
import com.google.eclipse.protobuf.protobuf.Group;
import com.google.eclipse.protobuf.protobuf.IndexedElement;
import com.google.eclipse.protobuf.protobuf.MessageField;

/**
 * Tests for <code>{@link IndexedElements#indexFeatureOf(IndexedElement)}</code>
 *
 * @author alruiz@google.com (Alex Ruiz)
 */
public class IndexedElements_indexFeatureOf_Test {
  @Rule public XtextRule xtext = overrideRuntimeModuleWith(unitTestModule());

  private static IndexedElements indexedElements;

  @BeforeClass public static void setUpOnce() {
    indexedElements = new IndexedElements();
  }

  // syntax = "proto2";
  //
  // message Person {
  //  optional String firstName = 6;
  // }
  @Test public void should_return_index_feature_of_MessageField() {
    MessageField field = xtext.find("firstName", MessageField.class);
    EStructuralFeature expected = MESSAGE_FIELD__INDEX;
    assertThat(indexedElements.indexFeatureOf(field), equalTo(expected));
  }

  // syntax = "proto2";
  //
  // message Person {
  //  optional group Names = 8 {}
  // }
  @Test public void should_return_name_of_Group() {
    Group group = xtext.find("Names", Group.class);
    EStructuralFeature expected = GROUP__INDEX;
    assertThat(indexedElements.indexFeatureOf(group), equalTo(expected));
  }

  @Test public void should_return_null_if_IndexedElement_is_null() {
    assertNull(indexedElements.indexFeatureOf(null));
  }
}
