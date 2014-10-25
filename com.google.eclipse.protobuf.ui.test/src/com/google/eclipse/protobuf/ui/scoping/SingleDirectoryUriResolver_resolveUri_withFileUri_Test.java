/*
 * Copyright (c) 2011 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.ui.scoping;

import static com.google.eclipse.protobuf.junit.core.UnitTestModule.unitTestModule;
import static com.google.eclipse.protobuf.junit.core.XtextRule.overrideRuntimeModuleWith;
import static org.eclipse.emf.common.util.URI.createURI;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;

import com.google.eclipse.protobuf.junit.core.AbstractTestModule;
import com.google.eclipse.protobuf.junit.core.XtextRule;
import com.google.eclipse.protobuf.util.Uris;
import com.google.inject.Inject;

import org.eclipse.emf.common.util.URI;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;

/**
 * Tests for {@link SingleDirectoryUriResolver#resolveUri(String, URI)}.
 *
 * @author alruiz@google.com (Alex Ruiz)
 */
public class SingleDirectoryUriResolver_resolveUri_withFileUri_Test {
  private static URI resourceUri;

  @BeforeClass public static void setUpOnce() {
    resourceUri = createURI("file:/usr/local/project/src/proto/person.proto");
  }

  @Rule public XtextRule xtext = overrideRuntimeModuleWith(unitTestModule(), new TestModule());

  @Inject private Uris uris;
  @Inject private SingleDirectoryUriResolver resolver;

  @Test public void should_resolve_import_URI() {
    uris().shouldAnyUriExist(true);
    String resolved = resolver.resolveUri("folder1/address.proto", resourceUri);
    assertThat(resolved, equalTo("file:/usr/local/project/src/proto/folder1/address.proto"));
  }

  private UrisStub uris() {
    return (UrisStub) uris;
  }

  private static class TestModule extends AbstractTestModule {
    @Override protected void configure() {
      binder().bind(Uris.class).toInstance(UrisStub.instance());
    }
  }
}
