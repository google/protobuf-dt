/*
 * Copyright (c) 2011 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.ui.protoc.command;

import static org.junit.rules.ExpectedException.none;
import static org.mockito.Mockito.*;

import org.junit.*;
import org.junit.rules.ExpectedException;

import com.google.eclipse.protobuf.ui.preferences.StringPreference;
import com.google.eclipse.protobuf.ui.preferences.compiler.core.CompilerPreferences;

/**
 * Tests for <code>{@link DescriptorPathProtocOption#addOptionTo(ProtocCommand)}</code>.
 *
 * @author alruiz@google.com (Alex Ruiz)
 */
public class DescriptorPathProtocOption_appendOptionToCommand_Test {
  @Rule public ExpectedException thrown = none();

  private StringPreference descriptorPath;
  private CompilerPreferences preferences;
  private ProtocCommand command;
  private DescriptorPathProtocOption option;

  @Before public void setUp() {
    descriptorPath = mock(StringPreference.class);
    preferences = mock(CompilerPreferences.class);
    command = mock(ProtocCommand.class);
    option = new DescriptorPathProtocOption(preferences, "/");
  }

  @Test public void should_not_append_to_command_if_descriptor_path_is_null() {
    expectDescriptorPathToBeEqualTo(null);
    option.addOptionTo(command);
    verifyZeroInteractions(command);
  }

  @Test public void should_not_append_to_command_if_descriptor_path_is_empty() {
    expectDescriptorPathToBeEqualTo("");
    option.addOptionTo(command);
    verifyZeroInteractions(command);
  }

  @Test public void should_throw_error_if_descriptor_path_does_not_contain_descriptor_FQN() {
    thrown.expect(IllegalArgumentException.class);
    thrown.expectMessage("Path '/usr/local/include' does not contain '/google/protobuf/descriptor.proto'");
    expectDescriptorPathToBeEqualTo("/usr/local/include");
    option.addOptionTo(command);
    verifyZeroInteractions(command);
  }

  @Test public void should_append_path_of_descriptor_to_command() {
    expectDescriptorPathToBeEqualTo("/usr/local/include/google/protobuf/descriptor.proto");
    option.addOptionTo(command);
    verify(command).appendOption("proto_path", "/usr/local/include");
  }

  private void expectDescriptorPathToBeEqualTo(String value) {
    when(preferences.descriptorPath()).thenReturn(descriptorPath);
    when(descriptorPath.getValue()).thenReturn(value);
  }
}
