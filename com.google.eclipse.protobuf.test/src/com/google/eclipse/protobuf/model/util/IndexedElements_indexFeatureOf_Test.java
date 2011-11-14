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
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import com.google.eclipse.protobuf.protobuf.*;

import org.eclipse.emf.ecore.EStructuralFeature;
import org.junit.*;
import static com.google.eclipse.protobuf.protobuf.ProtobufPackage.Literals.*;

/**
 * Tests for <code>{@link IndexedElements#indexFeatureOf(IndexedElement)}</code>
 * 
 * @author alruiz@google.com (Alex Ruiz)
 */
public class IndexedElements_indexFeatureOf_Test {

  private static IndexedElements indexedElements;
  
  @BeforeClass public static void setUpOnce() {
    indexedElements = new IndexedElements();
  }
  
  @Test public void should_return_name_of_Property() {
    MessageField field = mock(MessageField.class);
    EStructuralFeature expected = MESSAGE_FIELD__INDEX;
    assertThat(indexedElements.indexFeatureOf(field), equalTo(expected));
  }

  @Test public void should_return_name_of_Group() {
    Group group = mock(Group.class);
    EStructuralFeature expected = GROUP__INDEX;
    assertThat(indexedElements.indexFeatureOf(group), equalTo(expected));
  }
  
  @Test public void should_return_null_if_IndexedElement_is_null() {
    assertNull(indexedElements.indexFeatureOf(null));
  }
}
