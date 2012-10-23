/*
 * Copyright (c) 2012 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.ui.protoc.command;

import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import java.io.File;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

/**
 * Tests for <code>{@link ImportRootsProtocOption#singleImportRoot(File, File)}</code>.
 *
 * @author alruiz@google.com (Alex Ruiz)
 */
public class ImportRootsProtocOption_singleImportRoot_Test {
  @Rule public TemporaryFolder temporaryFolder = new TemporaryFolder();

  private File project;

  @Before public void setUp() {
    project = temporaryFolder.newFolder("project");
  }

  @Test public void should_return_project_directory_if_file_is_underneath_it() {
    File protoFile = new File(project, "test.proto");
    String singleImportRoot = ImportRootsProtocOption.singleImportRoot(project, protoFile);
    assertThat(singleImportRoot, equalTo(project.toString()));
  }

  @Test public void should_return_parent_directory_directly_below_project() {
    File srcDirectory = createDirectory(project, "src");
    File protoFile = new File(createDirectory(srcDirectory, "proto"), "test.proto");
    String singleImportRoot = ImportRootsProtocOption.singleImportRoot(project, protoFile);
    assertThat(singleImportRoot, equalTo(srcDirectory.toString()));
  }

  private File createDirectory(File parent, String name) {
    File directory = new File(parent, name);
    assertTrue(directory.mkdir());
    return directory;
  }
}
