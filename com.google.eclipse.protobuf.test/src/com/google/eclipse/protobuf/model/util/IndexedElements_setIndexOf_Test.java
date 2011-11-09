/*
 * Copyright (c) 2011 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.model.util;

import static org.mockito.Mockito.*;

import com.google.eclipse.protobuf.protobuf.*;

import org.junit.*;

/**
 * Tests for <code>{@link IndexedElements#setIndexTo(IndexedElement, long)}</code>
 * 
 * @author alruiz@google.com (Alex Ruiz)
 */
public class IndexedElements_setIndexOf_Test {

  private static IndexedElements indexedElements;
  
  @BeforeClass public static void setUpOnce() {
    indexedElements = new IndexedElements();
  }
  
  @Test public void should_return_name_of_Property() {
    Property p = mock(Property.class);
    indexedElements.setIndexTo(p, 6L);
    verify(p).setIndex(6L);
  }

  @Test public void should_return_name_of_Group() {
    Group g = mock(Group.class);
    indexedElements.setIndexTo(g, 8L);
    verify(g).setIndex(8L);
  }
}
