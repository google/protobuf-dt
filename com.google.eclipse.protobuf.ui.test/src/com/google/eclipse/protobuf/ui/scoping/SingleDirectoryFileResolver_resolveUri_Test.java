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
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

import org.eclipse.emf.common.util.URI;
import org.junit.*;

import com.google.eclipse.protobuf.ui.preferences.pages.paths.PathsPreferences;
import com.google.eclipse.protobuf.ui.util.Resources;

/**
 * Tests for <code>{@link SingleDirectoryFileResolver#resolveUri(String, URI, PathsPreferences)}</code>.
 *
 * @author alruiz@google.com (Alex Ruiz)
 */
public class SingleDirectoryFileResolver_resolveUri_Test {

  private static URI resourceUri;
  private static PathsPreferences preferences;

  @BeforeClass public static void setUpOnce() {
    resourceUri = createURI("platform:/resource/src/proto/person.proto");
    preferences = mock(PathsPreferences.class);
  }

  private Resources resources;
  private SingleDirectoryFileResolver resolver;

  @Before public void setUp() {
    resources = mock(Resources.class);
    resolver = new SingleDirectoryFileResolver(resources);
  }

  @Test public void should_resolve_import_URI_if_missing_scheme() {
    when(resources.fileExists(any(URI.class))).thenReturn(true);
    String resolved = resolver.resolveUri("folder1/address.proto", resourceUri, preferences);
    assertThat(resolved, equalTo("platform:/resource/src/proto/folder1/address.proto"));
  }

  @Test public void should_resolve_import_URI_even_if_overlapping_folders_with_resource_URI() {
    when(resources.fileExists(any(URI.class))).thenReturn(true);
    String resolved = resolver.resolveUri("src/proto/folder1/address.proto", resourceUri, preferences);
    assertThat(resolved, equalTo("platform:/resource/src/proto/folder1/address.proto"));
  }

  @Test public void should_resolve_import_URI_even_if_overlapping_one_folder_only_with_resource_URI() {
    when(resources.fileExists(any(URI.class))).thenReturn(true);
    String resolved = resolver.resolveUri("src/proto/read-only/address.proto", resourceUri, preferences);
    assertThat(resolved, equalTo("platform:/resource/src/proto/read-only/address.proto"));
  }
}
