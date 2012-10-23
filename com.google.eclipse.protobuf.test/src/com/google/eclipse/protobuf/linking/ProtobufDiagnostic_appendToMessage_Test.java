/*
 * Copyright (c) 2011 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.linking;

import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;

import org.eclipse.xtext.nodemodel.INode;
import org.junit.Before;
import org.junit.Test;

/**
 * Tests for <code>{@link ProtobufDiagnostic#appendToMessage(String)}</code>
 *
 * @author alruiz@google.com (Alex Ruiz)
 */
public class ProtobufDiagnostic_appendToMessage_Test {
  private ProtobufDiagnostic diagnostic;

  @Before public void setUp() {
    diagnostic = new ProtobufDiagnostic("1000", new String[0], "Hello", mock(INode.class));
  }

  @Test public void should_append_text_to_message() {
    diagnostic.appendToMessage(" ");
    diagnostic.appendToMessage("World");
    diagnostic.appendToMessage("!");
    assertThat(diagnostic.getMessage(), equalTo("Hello World!"));
  }
}
