/*
 * Copyright (c) 2011 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.util;

import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;

import java.util.List;

import org.junit.*;

import com.google.eclipse.protobuf.junit.core.XtextRule;
import com.google.eclipse.protobuf.protobuf.*;

/**
 * Tests for <code>{@link ProtobufElementFinder#importsIn(Protobuf)}</code>.
 *
 * @author alruiz@google.com (Alex Ruiz)
 */
public class ProtobufElementFinder_importsIn_Test {

  @Rule public XtextRule xtext = new XtextRule();

  private ProtobufElementFinder finder;

  @Before public void setUp() {
    finder = xtext.getInstanceOf(ProtobufElementFinder.class);
  }

  @Test public void should_return_all_imports() {
    StringBuilder proto = new StringBuilder();
    proto.append("import \"luke.proto\"")
         .append("import \"leia.proto\"");
    Protobuf root = xtext.parse(proto);
    List<Import> allImports = finder.importsIn(root);
    assertThat(allImports.size(), equalTo(2));
    assertThat(allImports.get(0).getImportURI(), equalTo("luke.proto"));
    assertThat(allImports.get(1).getImportURI(), equalTo("leia.proto"));
  }

  @Test public void should_return_empty_if_no_imports_found() {
    StringBuilder proto = new StringBuilder();
    proto.append("enum PhoneType {")
         .append("  MOBILE = 0;   ")
         .append("  HOME = 1;     ")
         .append("  WORK = 2;     ")
         .append("}               ");
    Protobuf root = xtext.parse(proto);
    List<Import> allImports = finder.importsIn(root);
    assertThat(allImports.size(), equalTo(0));
  }
}
