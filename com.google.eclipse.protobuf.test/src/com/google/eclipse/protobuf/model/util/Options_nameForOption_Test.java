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
 * Tests for <code>{@link Options#nameForOption(IndexedElement)}</code>
 * @author alruiz@google.com (Alex Ruiz)
 */
public class Options_nameForOption_Test {

  private static Options options;
  
  @BeforeClass public static void setUpOnce() {
    options = new Options();
  }
  
  @Test public void should_return_unchanged_name_if_element_is_Property() {
    Property p = mock(Property.class);
    when(p.getName()).thenReturn("active");
    assertThat(options.nameForOption(p), equalTo("active"));
    verify(p).getName();
  }
  
  @Test public void should_return_name_in_lower_case_if_element_is_Group() {
    Group g = mock(Group.class);
    when(g.getName()).thenReturn("Person");
    assertThat(options.nameForOption(g), equalTo("person"));
    verify(g).getName();
  }
  
  @Test public void should_return_null_if_element_is_null() {
    assertNull(options.nameForOption(null));
  }
}
