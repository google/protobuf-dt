/*
 * Copyright (c) 2012 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.ui.scoping;

import static java.util.Collections.singletonList;

import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import static com.google.eclipse.protobuf.junit.core.UnitTestModule.unitTestModule;
import static com.google.eclipse.protobuf.junit.core.XtextRule.overrideRuntimeModuleWith;

import java.io.File;
import java.io.IOException;

import org.eclipse.emf.common.util.URI;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.xtext.ui.editor.preferences.IPreferenceStoreAccess;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import com.google.eclipse.protobuf.junit.core.AbstractTestModule;
import com.google.eclipse.protobuf.junit.core.XtextRule;
import com.google.eclipse.protobuf.ui.preferences.paths.DirectoryPath;
import com.google.eclipse.protobuf.ui.preferences.paths.PathsPreferences;
import com.google.inject.Inject;

/**
 * Tests for <code>{@link MultipleDirectoriesFileResolverStrategy#resolveUri(String, URI, Iterable)}</code>.
 *
 * @author alruiz@google.com (Alex Ruiz)
 */
public class MultipleDirectoriesFileResolverStrategy_resolveUri_Test {
  private static URI declaringResourceUri;

  @BeforeClass public static void setUpOnce() {
    declaringResourceUri = URI.createURI("platform:/resource/project/src/test.proto");
  }

  @Rule public XtextRule xtext = overrideRuntimeModuleWith(unitTestModule(), new TestModule());
  @Rule public TemporaryFolder temporaryFolder = new TemporaryFolder();

  @Inject private ResourceLocations locations;
  @Inject private UriResolver uriResolver;

  @Inject private MultipleDirectoriesFileResolverStrategy strategy;

  private IPreferenceStore store;
  private PathsPreferences preferences;
  private Iterable<PathsPreferences> allPreferences;

  @Before public void setUp() {
    IPreferenceStoreAccess storeAccess = mock(IPreferenceStoreAccess.class);
    store = mock(IPreferenceStore.class);
    when(storeAccess.getWritablePreferenceStore(null)).thenReturn(store);
    preferences = new PathsPreferences(storeAccess , null);
    allPreferences = singletonList(preferences);
  }

  @Test public void should_resolve_platform_resource_URI() {
    String importUri = "imported.proto";
    String expected = "platform:/resource/src/protos/" + importUri;
    when(store.getString("paths.directoryPaths")).thenReturn("${workspace_loc:/src/protos}");
    when(uriResolver.resolveUri(eq(importUri), any(DirectoryPath.class))).thenReturn(expected);
    String resolved = strategy.resolveUri(importUri, declaringResourceUri, allPreferences);
    assertThat(resolved, equalTo(expected));
  }

  @Test public void should_resolve_file_URI() throws IOException {
    String importUri = "imported.proto";
    File file = temporaryFolder.newFile(importUri);
    String expected = file.toURI().toString();
    when(store.getString("paths.directoryPaths")).thenReturn(temporaryFolder.getRoot().toString());
    when(uriResolver.resolveUri(eq(importUri), any(DirectoryPath.class))).thenReturn(expected);
    String resolved = strategy.resolveUri(importUri, declaringResourceUri, allPreferences);
    assertThat(resolved, equalTo(expected));
  }

  @Test public void should_fall_back_to_file_system_if_platform_resource_URI_cannot_be_resolved() throws IOException {
    String importUri = "imported.proto";
    File file = temporaryFolder.newFile(importUri);
    String expected = file.toURI().toString();
    when(store.getString("paths.directoryPaths")).thenReturn("${workspace_loc:/src/protos}");
    // try the first time as resource platform
    when(uriResolver.resolveUri(eq(importUri), any(DirectoryPath.class))).thenReturn(null);
    // try again, but in the file system this time
    String directoryLocation = temporaryFolder.getRoot().toString();
    when(locations.directoryLocation("/src/protos")).thenReturn(directoryLocation);
    when(uriResolver.resolveUriInFileSystem(importUri, directoryLocation)).thenReturn(expected);
    String resolved = strategy.resolveUri(importUri, declaringResourceUri, allPreferences);
    assertThat(resolved, equalTo(expected));
  }

  @Test public void should_return_null_if_URI_cannot_be_resolved() {
    when(store.getString("paths.directoryPaths")).thenReturn("${workspace_loc:/src/protos}");
    String importUri = "imported.proto";
    when(uriResolver.resolveUri(eq(importUri), any(DirectoryPath.class))).thenReturn(null);
    String resolved = strategy.resolveUri(importUri, declaringResourceUri, allPreferences);
    assertNull(resolved);
  }

  private static class TestModule extends AbstractTestModule {
    @Override protected void configure() {
      mockAndBind(UriResolver.class);
      mockAndBind(ResourceLocations.class);
    }
  }
}
