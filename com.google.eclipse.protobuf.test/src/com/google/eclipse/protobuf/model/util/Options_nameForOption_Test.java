/*
 * Copyright (c) 2011 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.model.util;

import static com.google.eclipse.protobuf.junit.core.Setups.unitTestSetup;
import static com.google.eclipse.protobuf.junit.core.XtextRule.createWith;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import org.junit.*;

import com.google.eclipse.protobuf.junit.core.XtextRule;
import com.google.eclipse.protobuf.protobuf.*;

/**
 * Tests for <code>{@link Options#nameForOption(IndexedElement)}</code>
 * @author alruiz@google.com (Alex Ruiz)
 */
public class Options_nameForOption_Test {

  @Rule public XtextRule xtext = createWith(unitTestSetup());

  private Options options;

  @Before public void setUp() {
    options = xtext.getInstanceOf(Options.class);
  }

  @Test public void should_return_unchanged_name_if_element_is_Field() {
    MessageField field = mock(MessageField.class);
    when(field.getName()).thenReturn("active");
    assertThat(options.nameForOption(field), equalTo("active"));
    verify(field).getName();
  }

  @Test public void should_return_name_in_lower_case_if_element_is_Group() {
    Group group = mock(Group.class);
    when(group.getName()).thenReturn("Person");
    assertThat(options.nameForOption(group), equalTo("person"));
    verify(group).getName();
  }

  @Test public void should_return_null_if_element_is_null() {
    assertNull(options.nameForOption(null));
  }
}
