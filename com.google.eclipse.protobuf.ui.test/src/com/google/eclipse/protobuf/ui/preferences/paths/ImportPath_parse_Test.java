/*
 * Copyright (c) 2011 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.ui.preferences.paths;

import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;

import org.junit.Test;

/**
 * Tests for <code>{@link ImportPath#parse(String)}</code>
 * 
 * @author alruiz@google.com (Alex Ruiz)
 */
public class ImportPath_parse_Test {

  @Test public void should_parse_workspace_path() {
    ImportPath path = ImportPath.parse("${workspace_loc:/test/src}");
    assertThat(path.value, equalTo("/test/src"));
    assertThat(path.isWorkspacePath, equalTo(true));
  }

  @Test public void should_parse_file_system_path() {
    ImportPath path = ImportPath.parse("/test/src");
    assertThat(path.value, equalTo("/test/src"));
    assertThat(path.isWorkspacePath, equalTo(false));
  }
}
