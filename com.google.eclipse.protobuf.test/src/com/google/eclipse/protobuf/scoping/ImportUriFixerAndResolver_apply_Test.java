/*
 * Copyright (c) 2011 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.scoping;

import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.xtext.scoping.impl.ImportUriResolver;
import org.junit.*;

import com.google.eclipse.protobuf.junit.core.XtextRule;
import com.google.eclipse.protobuf.protobuf.Import;
import com.google.eclipse.protobuf.protobuf.Protobuf;

/**
 * Tests for <code>{@link ImportUriFixerAndResolver#apply(EObject)}</code>.
 *
 * @author alruiz@google.com (Alex Ruiz)
 */
public class ImportUriFixerAndResolver_apply_Test {

  @Rule public XtextRule xtext = new XtextRule();
  
  private ImportUriFixerAndResolver resolver;
  
  @Before public void setUp() {
    resolver = (ImportUriFixerAndResolver) xtext.getInstanceOf(ImportUriResolver.class);
  }
  
  @Test public void should_fix_import_URI_if_missing_scheme() {
    StringBuilder proto = new StringBuilder();
    proto.append("import \"folder1/test.proto\";"); 
    Protobuf root = xtext.parse(proto);
    Import anImport = root.getImports().get(0);
    String resolved = resolver.apply(anImport);
    assertThat(resolved, equalTo("platform:/resource/folder1/test.proto"));
  }

  
  @Test public void should_not_fix_import_URI_if_not_missing_scheme() {
    StringBuilder proto = new StringBuilder();
    proto.append("import \"platform:/resource/folder1/test.proto\";"); 
    Protobuf root = xtext.parse(proto);
    Import anImport = root.getImports().get(0);
    String resolved = resolver.apply(anImport);
    assertThat(resolved, equalTo("platform:/resource/folder1/test.proto"));
  }
}
