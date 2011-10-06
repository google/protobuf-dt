/*
 * Copyright (c) 2011 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.model.util;

import static com.google.eclipse.protobuf.junit.core.Setups.unitTestSetup;
import static com.google.eclipse.protobuf.junit.core.XtextRule.createWith;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;

import com.google.eclipse.protobuf.junit.core.XtextRule;
import com.google.eclipse.protobuf.protobuf.*;

import org.junit.*;

import java.util.List;

/**
 * Tests for <code>{@link ModelFinder#importsIn(Protobuf)}</code>.
 *
 * @author alruiz@google.com (Alex Ruiz)
 */
public class ModelFinder_importsIn_Test {

  @Rule public XtextRule xtext = createWith(unitTestSetup());

  private Protobuf root;
  private ModelFinder finder;

  @Before public void setUp() {
    root = xtext.root();
    finder = xtext.getInstanceOf(ModelFinder.class);
  }

  // import "luke.proto";
  // import "leia.proto";
  @Test public void should_return_all_imports() {
    List<Import> allImports = finder.importsIn(root);
    assertThat(allImports.size(), equalTo(2));
    assertThat(allImports.get(0).getImportURI(), equalTo("luke.proto"));
    assertThat(allImports.get(1).getImportURI(), equalTo("leia.proto"));
  }

  // enum PhoneType {
  //   MOBILE = 0;
  //   HOME = 1;
  //   WORK = 2;
  // }
  @Test public void should_return_empty_if_no_imports_found() {
    List<Import> allImports = finder.importsIn(root);
    assertThat(allImports.size(), equalTo(0));
  }
}
