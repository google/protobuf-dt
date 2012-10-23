/*
 * Copyright (c) 2012 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.ui.scoping;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertNull;

import java.io.File;
import java.util.UUID;

import org.hamcrest.core.IsEqual;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

/**
 * Tests for <code>{@link FileSystemPathResolver#resolvePath(String)}</code>.
 *
 * @author alruiz@google.com (Alex Ruiz)
 */
public class FileSystemPathResolver_resolvePath_Test {
  @Rule public TemporaryFolder temporaryFolder = new TemporaryFolder();
  
  private FileSystemPathResolver resolver;
  
  @Before public void setUp() {
    resolver = new FileSystemPathResolver();
  }
  
  @Test public void should_resolve_path_if_directory_exists() {
    File root = temporaryFolder.getRoot();
    String path = root.getAbsolutePath();
    assertThat(resolver.resolvePath(path), IsEqual.equalTo(root.toURI().getPath()));
  }
  
  @Test public void should_return_null_if_path_is_null() {
    assertNull(resolver.resolvePath(null));
  }

  @Test public void should_return_null_if_path_is_empty() {
    assertNull(resolver.resolvePath(""));
  }

  @Test public void should_return_null_if_path_does_not_exist() {
    String path = UUID.randomUUID().toString();
    assertNull(resolver.resolvePath(path));
  }
}
