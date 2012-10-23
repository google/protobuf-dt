/*
 * Copyright (c) 2011 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.ui.protoc.command;

import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;
import static org.junit.rules.ExpectedException.none;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import com.google.eclipse.protobuf.ui.preferences.compiler.CompilerPreferences;

/**
 * Tests for <code>{@link DescriptorPathProtocOption#addOptionTo(ProtocCommand)}</code>.
 *
 * @author alruiz@google.com (Alex Ruiz)
 */
public class DescriptorPathProtocOption_appendOptionToCommand_Test {
  @Rule public ExpectedException thrown = none();

  private CompilerPreferences preferences;
  private ProtocCommand command;
  private DescriptorPathProtocOption option;

  @Before public void setUp() {
    preferences = mock(CompilerPreferences.class);
    command = new ProtocCommand("protoc");
    option = new DescriptorPathProtocOption(preferences, "/");
  }

  @Test public void should_not_append_to_command_if_descriptor_path_is_null() {
    when(preferences.descriptorPath()).thenReturn(null);
    option.addOptionTo(command);
    assertThat(command.toString(), equalTo("protoc"));
  }

  @Test public void should_not_append_to_command_if_descriptor_path_is_empty() {
    when(preferences.descriptorPath()).thenReturn("");
    option.addOptionTo(command);
    assertThat(command.toString(), equalTo("protoc"));
  }

  @Test public void should_throw_error_if_descriptor_path_does_not_contain_descriptor_FQN() {
    thrown.expect(IllegalArgumentException.class);
    thrown.expectMessage("Path '/usr/local/include' does not contain '/google/protobuf/descriptor.proto'");
    when(preferences.descriptorPath()).thenReturn("/usr/local/include");
    option.addOptionTo(command);
    assertThat(command.toString(), equalTo("protoc"));
  }

  @Test public void should_append_path_of_descriptor_to_command() {
    when(preferences.descriptorPath()).thenReturn("/usr/local/include/google/protobuf/descriptor.proto");
    option.addOptionTo(command);
    assertThat(command.toString(), equalTo("protoc --proto_path=/usr/local/include"));
  }
}
