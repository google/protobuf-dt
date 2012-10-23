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
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import static com.google.eclipse.protobuf.junit.core.UnitTestModule.unitTestModule;
import static com.google.eclipse.protobuf.junit.core.XtextRule.overrideRuntimeModuleWith;

import org.junit.Rule;
import org.junit.Test;

import com.google.eclipse.protobuf.junit.core.XtextRule;
import com.google.eclipse.protobuf.protobuf.Message;
import com.google.eclipse.protobuf.protobuf.TypeExtension;
import com.google.inject.Inject;

/**
 * Tests for <code>{@link TypeExtensions#messageFrom(TypeExtension)}</code>.
 *
 * @author alruiz@google.com (Alex Ruiz)
 */
public class TypeExtensions_messageFrom_Test {
  @Rule public XtextRule xtext = overrideRuntimeModuleWith(unitTestModule());

  @Inject private TypeExtensions typeExtensions;

  // syntax = "proto2";
  //
  // message Person {
  //   optional string name = 1;
  // }
  //
  // extend Person {}
  @Test public void should_return_message_from_extension() {
    TypeExtension extension = xtext.find("Person", " {}", TypeExtension.class);
    Message message = typeExtensions.messageFrom(extension);
    assertThat(message.getName(), equalTo("Person"));
  }

  @Test public void should_return_null_if_extension_is_not_referring_to_message() {
    TypeExtension extension = mock(TypeExtension.class);
    when(extension.getType()).thenReturn(null);
    assertNull(typeExtensions.messageFrom(extension));
  }
}
