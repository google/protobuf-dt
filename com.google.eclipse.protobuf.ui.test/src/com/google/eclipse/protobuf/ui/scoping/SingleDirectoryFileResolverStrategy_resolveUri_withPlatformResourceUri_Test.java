/*
 * Copyright (c) 2011 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.ui.scoping;

import static org.eclipse.emf.common.util.URI.createURI;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;

import static com.google.eclipse.protobuf.junit.core.UnitTestModule.unitTestModule;
import static com.google.eclipse.protobuf.junit.core.XtextRule.overrideRuntimeModuleWith;

import org.eclipse.emf.common.util.URI;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;

import com.google.eclipse.protobuf.junit.core.AbstractTestModule;
import com.google.eclipse.protobuf.junit.core.XtextRule;
import com.google.eclipse.protobuf.util.Uris;
import com.google.inject.Inject;

/**
 * Tests for <code>{@link SingleDirectoryFileResolverStrategy#resolveUri(String, URI, Iterable)}</code>.
 *
 * @author alruiz@google.com (Alex Ruiz)
 */
public class SingleDirectoryFileResolverStrategy_resolveUri_withPlatformResourceUri_Test {
  private static URI resourceUri;

  @BeforeClass public static void setUpOnce() {
    resourceUri = createURI("platform:/resource/src/proto/person.proto");
  }

  @Rule public XtextRule xtext = overrideRuntimeModuleWith(unitTestModule(), new TestModule());

  private @Inject Uris uris;
  private @Inject SingleDirectoryFileResolverStrategy resolver;

  @Test public void should_resolve_import_URI_if_missing_scheme() {
    uris().shouldAnyUriExist(true);
    String resolved = resolver.resolveUri("folder1/address.proto", resourceUri, null);
    assertThat(resolved, equalTo("platform:/resource/src/proto/folder1/address.proto"));
  }

  @Test public void should_resolve_import_URI_when_overlapping_folders_with_resource_URI() {
    uris().shouldAnyUriExist(true);
    String resolved = resolver.resolveUri("src/proto/folder1/address.proto", resourceUri, null);
    assertThat(resolved, equalTo("platform:/resource/src/proto/folder1/address.proto"));
  }

  @Test public void should_resolve_import_URI_when_overlapping_one_folder_only_with_resource_URI() {
    uris().shouldAnyUriExist(true);
    String resolved = resolver.resolveUri("src/proto/read-only/address.proto", resourceUri, null);
    assertThat(resolved, equalTo("platform:/resource/src/proto/read-only/address.proto"));
  }

  @Test public void should_return_null_if_URI_cannot_be_resolved() {
    uris().shouldAnyUriExist(false);
    String resolved = resolver.resolveUri("src/proto/read-only/person.proto", resourceUri, null);
    assertNull(resolved);
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
