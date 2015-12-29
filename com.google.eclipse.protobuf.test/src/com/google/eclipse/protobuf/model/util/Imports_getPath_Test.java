/*
 * Copyright (c) 2014 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.model.util;

import static com.google.eclipse.protobuf.junit.core.IntegrationTestModule.integrationTestModule;
import static com.google.eclipse.protobuf.junit.core.XtextRule.overrideRuntimeModuleWith;
import static org.eclipse.xtext.EcoreUtil2.getAllContentsOfType;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;

import com.google.eclipse.protobuf.junit.core.XtextRule;
import com.google.eclipse.protobuf.protobuf.Import;
import com.google.eclipse.protobuf.protobuf.Protobuf;
import com.google.inject.Inject;

import org.junit.Rule;
import org.junit.Test;

import java.util.List;

/**
 * Tests for <code>{@link Imports#getPath(Import)}</code>
 *
 * @author alruiz@google.com (Alex Ruiz)
 */
public class Imports_getPath_Test {
  @Rule public XtextRule xtext = overrideRuntimeModuleWith(integrationTestModule());

  @Inject private Imports imports;

  // syntax = "proto2";
  // import "types.proto";
  @Test public void should_return_import_URI_as_entered_by_user() {
    Protobuf root = xtext.root();
    Import anImport = firstImportOf(root);
    assertThat(imports.getPath(anImport), equalTo("types.proto"));
  }

  private Import firstImportOf(Protobuf root) {
    List<Import> allImports = getAllContentsOfType(root, Import.class);
    return allImports.get(0);
  }
}
