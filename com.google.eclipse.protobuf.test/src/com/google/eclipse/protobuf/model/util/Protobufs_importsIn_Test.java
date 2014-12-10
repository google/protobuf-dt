/*
 * Copyright (c) 2014 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.model.util;

import static com.google.eclipse.protobuf.junit.core.UnitTestModule.unitTestModule;
import static com.google.eclipse.protobuf.junit.core.XtextRule.overrideRuntimeModuleWith;
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
 * Tests for <code>{@link Protobufs#importsIn(Protobuf)}</code>.
 *
 * @author alruiz@google.com (Alex Ruiz)
 */
public class Protobufs_importsIn_Test {
  @Rule public XtextRule xtext = overrideRuntimeModuleWith(unitTestModule());

  @Inject private Protobufs protobufs;
  @Inject private Imports imports;

  // syntax = "proto2";
  //
  // import "luke.proto";
  // import "leia.proto";
  @Test public void should_return_all_imports() {
    List<Import> allImports = protobufs.importsIn(xtext.root());
    assertThat(allImports.size(), equalTo(2));
    assertThat(imports.getPath(allImports.get(0)), equalTo("luke.proto"));
    assertThat(imports.getPath(allImports.get(1)), equalTo("leia.proto"));
  }

  // syntax = "proto2";
  //
  // enum PhoneType {
  //   MOBILE = 0;
  //   HOME = 1;
  //   WORK = 2;
  // }
  @Test public void should_return_empty_if_no_imports_found() {
    List<Import> allImports = protobufs.importsIn(xtext.root());
    assertThat(allImports.size(), equalTo(0));
  }
}
