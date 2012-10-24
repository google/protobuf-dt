/*
 * Copyright (c) 2012 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.util;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import static com.google.eclipse.protobuf.junit.core.UnitTestModule.unitTestModule;
import static com.google.eclipse.protobuf.junit.core.XtextRule.overrideRuntimeModuleWith;

import java.io.File;
import java.io.IOException;

import org.eclipse.emf.common.util.URI;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import com.google.eclipse.protobuf.junit.core.XtextRule;
import com.google.eclipse.protobuf.util.Uris;
import com.google.inject.Inject;

/**
 * Tests for <code>{@link Uris#referredResourceExists(URI)}</code>
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
    assertTrue(uris.referredResourceExists(fileUri));
  }

  @Test public void should_return_false_if_file_resource_does_not_exist() {
    URI fileUri = URI.createFileURI("/usr/local/not_existing_file.txt");
    assertFalse(uris.referredResourceExists(fileUri));
  }
}
