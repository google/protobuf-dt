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

import com.google.eclipse.protobuf.junit.core.XtextRule;
import com.google.eclipse.protobuf.protobuf.*;

import org.junit.*;

/**
 * Tests for <code>{@link ModelFinder#messageFrom(ExtendMessage)}</code>.
 *
 * @author alruiz@google.com (Alex Ruiz)
 */
public class ModelFinder_messageFrom_Test {

  @Rule public XtextRule xtext = createWith(unitTestSetup());

  private ModelFinder finder;

  @Before public void setUp() {
    finder = xtext.getInstanceOf(ModelFinder.class);
  }

  // message Person {
  //   optional string name = 1;
  // }
  //
  // extend Person {}
  @Test public void should_return_message_from_extension() {
    ExtendMessage extension = xtext.find("Person", " {}", ExtendMessage.class);
    Message message = finder.messageFrom(extension);
    assertThat(message.getName(), equalTo("Person"));
  }
  
  @Test public void should_return_null_if_extension_is_not_referring_to_message() {
    ExtendMessage extension = mock(ExtendMessage.class);
    when(extension.getMessage()).thenReturn(null);
    assertNull(finder.messageFrom(extension));
  }
}
