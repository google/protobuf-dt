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
 * Tests for <code>{@link DirectoryPath#toString()}</code>
 * 
 * @author alruiz@google.com (Alex Ruiz)
 */
public class DirectoryPath_toString_Test {

  @Test public void should_specify_is_workspace_path() {
    DirectoryPath path = new DirectoryPath("/test/src", true);
    assertThat(path.toString(), equalTo("${workspace_loc:/test/src}"));
  }

  @Test public void should_return_plain_value_if_it_is_not_workspace_path() {
    DirectoryPath path = new DirectoryPath("/test/src", false);
    assertThat(path.toString(), equalTo("/test/src"));
  }
}
