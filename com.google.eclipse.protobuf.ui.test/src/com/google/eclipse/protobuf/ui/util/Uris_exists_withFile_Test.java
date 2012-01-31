/*
 * Copyright (c) 2012 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.ui.util;

import static com.google.eclipse.protobuf.junit.core.UnitTestModule.unitTestModule;
import static com.google.eclipse.protobuf.junit.core.XtextRule.overrideRuntimeModuleWith;
import static org.junit.Assert.*;

import java.io.*;

import org.eclipse.emf.common.util.URI;
import org.junit.*;
import org.junit.rules.TemporaryFolder;

import com.google.eclipse.protobuf.junit.core.XtextRule;
import com.google.inject.Inject;

/**
 * Tests for <code>{@link Uris#exists(URI)}</code>
 *
 * @author alruiz@google.com (Alex Ruiz)
 */
public class Uris_exists_withFile_Test {
  @Rule public TemporaryFolder folder = new TemporaryFolder();
  @Rule public XtextRule xtext = overrideRuntimeModuleWith(unitTestModule());

  @Inject private Uris uris;

  @Test public void should_return_true_if_file_resource_exists() throws IOException {
    File file = folder.newFile("existing_file.txt");
    URI fileUri = URI.createFileURI(file.getAbsolutePath());
    assertTrue(uris.exists(fileUri));
  }

  @Test public void should_return_false_if_file_resource_does_not_exist() {
    URI fileUri = URI.createFileURI("/usr/local/not_existing_file.txt");
    assertFalse(uris.exists(fileUri));
  }
}
