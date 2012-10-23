/*
 * Copyright (c) 2012 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.model.util;

import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import static com.google.eclipse.protobuf.junit.core.UnitTestModule.unitTestModule;
import static com.google.eclipse.protobuf.junit.core.XtextRule.overrideRuntimeModuleWith;

import org.eclipse.emf.common.util.URI;
import org.eclipse.xtext.scoping.impl.ImportUriResolver;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import com.google.eclipse.protobuf.junit.core.AbstractTestModule;
import com.google.eclipse.protobuf.junit.core.XtextRule;
import com.google.eclipse.protobuf.protobuf.Import;
import com.google.inject.Inject;

/**
 * Tests for <code>{@link Imports#resolvedUriOf(Import)}</code>
 *
 * @author alruiz@google.com (Alex Ruiz)
 */
public class Imports_resolvedUriOf_Test {
  @Rule public XtextRule xtext = overrideRuntimeModuleWith(unitTestModule(), new TestModule());

  @Inject private ImportUriResolver uriResolver;
  @Inject private Imports imports;

  private Import anImport;

  @Before public void setUp() {
    anImport = mock(Import.class);
  }

  @Test public void should_return_resolved_URI() {
    when(uriResolver.apply(anImport)).thenReturn("file:/protos/test.proto");
    URI uri = imports.resolvedUriOf(anImport);
    assertThat(uri.path(), equalTo("/protos/test.proto"));
  }

  @Test public void should_return_null_if_URI_cannot_be_resolved() {
    when(uriResolver.apply(anImport)).thenReturn(null);
    URI uri = imports.resolvedUriOf(anImport);
    assertNull(uri);
  }

  private static class TestModule extends AbstractTestModule {
    @Override protected void configure() {
      mockAndBind(ImportUriResolver.class);
    }
  }
}
