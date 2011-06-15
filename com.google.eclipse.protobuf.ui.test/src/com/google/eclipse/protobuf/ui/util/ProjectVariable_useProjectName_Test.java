/*
 * Copyright (c) 2011 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.ui.util;

import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.*;

import org.eclipse.core.resources.IProject;
import org.junit.*;

/**
 * Tests for <code>{@link ProjectVariable#useProjectName(String, IProject)}</code>.
 * 
 * @author alruiz@google.com (Alex Ruiz)
 */
public class ProjectVariable_useProjectName_Test {

  private IProject project;
  
  @Before public void setUp() {
    project = mock(IProject.class);
  }
  
  @Test public void should_use_project_name_if_path_contains_variable() {
    when(project.getName()).thenReturn("test");
    String newPath = ProjectVariable.useProjectName("/${project}/src/test", project);
    assertThat(newPath, equalTo("/test/src/test"));
  }
  
  @Test public void should_not_use_project_name_if_path_does_not_contain_variable() {
    when(project.getName()).thenReturn("test");
    String newPath = ProjectVariable.useProjectName("/main/src/test", project);
    assertThat(newPath, equalTo("/main/src/test"));
  }

  @Test public void should_not_use_project_name_if_path_already_contains_it() {
    when(project.getName()).thenReturn("test");
    String newPath = ProjectVariable.useProjectName("/test/src/test", project);
    assertThat(newPath, equalTo("/test/src/test"));
  }
}
