/*
 * Copyright (c) 2011 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.model.util;

import static com.google.eclipse.protobuf.junit.core.UnitTestModule.unitTestModule;
import static com.google.eclipse.protobuf.junit.core.XtextRule.overrideRuntimeModuleWith;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import org.junit.*;

import com.google.eclipse.protobuf.junit.core.XtextRule;
import com.google.eclipse.protobuf.protobuf.*;

/**
 * Tests for <code>{@link ModelFinder#messageFrom(TypeExtension)}</code>.
 *
 * @author alruiz@google.com (Alex Ruiz)
 */
public class ModelFinder_messageFrom_Test {
  @Rule public XtextRule xtext = overrideRuntimeModuleWith(unitTestModule());

  private ModelFinder finder;

  @Before public void setUp() {
    finder = xtext.getInstanceOf(ModelFinder.class);
  }

  // syntax = "proto2";
  //
  // message Person {
  //   optional string name = 1;
  // }
  //
  // extend Person {}
  @Test public void should_return_message_from_extension() {
    TypeExtension extension = xtext.find("Person", " {}", TypeExtension.class);
    Message message = finder.messageFrom(extension);
    assertThat(message.getName(), equalTo("Person"));
  }

  @Test public void should_return_null_if_extension_is_not_referring_to_message() {
    TypeExtension extension = mock(TypeExtension.class);
    when(extension.getType()).thenReturn(null);
    assertNull(finder.messageFrom(extension));
  }
}
