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

import org.junit.*;

/**
 * Tests for <code>{@link IndexedElements#indexOf(IndexedElement)}</code>
 * 
 * @author alruiz@google.com (Alex Ruiz)
 */
public class IndexedElements_indexOf_Test {

  private static IndexedElements indexedElements;
  
  @BeforeClass public static void setUpOnce() {
    indexedElements = new IndexedElements();
  }
  
  @Test public void should_return_name_of_Property() {
    Property p = mock(Property.class);
    when(p.getIndex()).thenReturn(6L);
    assertThat(indexedElements.indexOf(p), equalTo(6L));
    verify(p).getIndex();
  }

  @Test public void should_return_name_of_Group() {
    Group g = mock(Group.class);
    when(g.getIndex()).thenReturn(8L);
    assertThat(indexedElements.indexOf(g), equalTo(8L));
    verify(g).getIndex();
  }
  
  @Test public void should_return_MIN_VALUE_if_IndexedElement_is_null() {
    assertThat(indexedElements.indexOf(null), equalTo(Long.MIN_VALUE));
  }
}
