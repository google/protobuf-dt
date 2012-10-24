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
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.junit.Before;
import org.junit.Test;

import com.google.eclipse.protobuf.ui.preferences.paths.ProjectVariable;

/**
 * Tests for <code>{@link ProjectVariable#replaceProjectNameWithProjectVariable(IPath, IProject)}</code>.
 *
 * @author alruiz@google.com (Alex Ruiz)
 */
public class ProjectVariable_replaceProjectNameWithProjectVariable_Test {
  private IProject project;

  @Before public void setUp() {
    project = mock(IProject.class);
  }

  @Test public void should_use_variable_if_path_contains_project_name() {
    IPath path = Path.fromOSString("/test/src/test");
    when(project.getName()).thenReturn("test");
    IPath newPath = ProjectVariable.replaceProjectNameWithProjectVariable(path, project);
    assertThat(newPath.toPortableString(), equalTo("/${project}/src/test"));
  }

  @Test public void should_not_use_variable_if_path_does_not_contain_project_name() {
    IPath path = Path.fromOSString("/main/src/test");
    when(project.getName()).thenReturn("test");
    IPath newPath = ProjectVariable.replaceProjectNameWithProjectVariable(path, project);
    assertThat(newPath.toPortableString(), equalTo("/main/src/test"));
  }

  @Test public void should_not_use_variable_if_path_already_contains_it() {
    IPath path = Path.fromOSString("/${project}/src/test");
    when(project.getName()).thenReturn("test");
    IPath newPath = ProjectVariable.replaceProjectNameWithProjectVariable(path, project);
    assertThat(newPath.toPortableString(), equalTo("/${project}/src/test"));
  }
}
