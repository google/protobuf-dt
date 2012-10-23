/*
 * Copyright (c) 2011 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.ui.preferences.paths.core;

import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.eclipse.core.resources.IProject;
import org.junit.Before;
import org.junit.Test;

import com.google.eclipse.protobuf.ui.preferences.paths.DirectoryPath;

/**
 * Tests for <code>{@link DirectoryPath#parse(String, IProject)}</code>
 *
 * @author alruiz@google.com (Alex Ruiz)
 */
public class DirectoryPath_parse_Test {
  private IProject project;

  @Before public void setUp() {
    project = mock(IProject.class);
  }

  @Test public void should_parse_workspace_path() {
    when(project.getName()).thenReturn("test");
    DirectoryPath path = DirectoryPath.parse("${workspace_loc:/${project}/src}", project);
    assertThat(path.value(), equalTo("/test/src"));
    assertTrue(path.isWorkspacePath());
  }

  @Test public void should_parse_workspace_path_with_null_IProject() {
    DirectoryPath path = DirectoryPath.parse("${workspace_loc:/test/src}", null);
    assertThat(path.value(), equalTo("/test/src"));
    assertTrue(path.isWorkspacePath());
  }

  @Test public void should_parse_file_system_path() {
    DirectoryPath path = DirectoryPath.parse("/test/src", project);
    assertThat(path.value(), equalTo("/test/src"));
    assertFalse(path.isWorkspacePath());
  }
}
