/*
 * Copyright (c) 2012 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.ui.scoping;

import static com.google.eclipse.protobuf.junit.core.UnitTestModule.unitTestModule;
import static com.google.eclipse.protobuf.junit.core.XtextRule.overrideRuntimeModuleWith;
import static java.util.Collections.singletonList;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

import org.eclipse.emf.common.util.URI;
import org.junit.*;

import com.google.eclipse.protobuf.junit.core.*;
import com.google.eclipse.protobuf.ui.preferences.StringPreference;
import com.google.eclipse.protobuf.ui.preferences.paths.core.PathsPreferences;
import com.google.eclipse.protobuf.ui.util.Uris;
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

  @Inject private ResourceLocations locations;
  @Inject private Uris uris;
  @Inject private MultipleDirectoriesFileResolverStrategy resolver;

  private StringPreference directoryPaths;
  private PathsPreferences preferences;
  private Iterable<PathsPreferences> allPreferences;

  @Before public void setUp() {
    directoryPaths = mock(StringPreference.class);
    preferences = mock(PathsPreferences.class);
    allPreferences = singletonList(preferences);
    when(preferences.directoryPaths()).thenReturn(directoryPaths);
  }

  @Test public void should_resolve_platform_resource_URI() {
    String expected = "platform:/resource/src/protos/imported.proto";
    when(directoryPaths.getValue()).thenReturn("${workspace_loc:/src/protos}");
    when(uris.referredResourceExists(URI.createURI(expected))).thenReturn(true);
    String resolved = resolver.resolveUri("imported.proto", declaringResourceUri, allPreferences);
    assertThat(resolved, equalTo(expected));
  }

  @Test public void should_resolve_file_URI() {
    String expected = "file:/usr/local/project/src/protos/imported.proto";
    when(directoryPaths.getValue()).thenReturn("/usr/local/project/src/protos");
    when(uris.referredResourceExists(URI.createURI(expected))).thenReturn(true);
    String resolved = resolver.resolveUri("imported.proto", declaringResourceUri, allPreferences);
    assertThat(resolved, equalTo(expected));
  }

  @Test public void should_fall_back_to_file_system_if_platform_resource_URI_cannot_be_resolved() {
    String expected = "file:/usr/local/project/src/protos/imported.proto";
    when(directoryPaths.getValue()).thenReturn("${workspace_loc:/src/protos}");
    // try the first time as resource platform
    when(uris.referredResourceExists(URI.createURI("platform:/resource/src/protos/imported.proto"))).thenReturn(false);
    // try again, but in the file system this time
    when(locations.directoryLocation("/src/protos")).thenReturn("/usr/local/project/src/protos");
    when(uris.referredResourceExists(URI.createURI(expected))).thenReturn(true);
    String resolved = resolver.resolveUri("imported.proto", declaringResourceUri, allPreferences);
    assertThat(resolved, equalTo(expected));
  }

  @Test public void should_return_null_if_platform_resource_URI_cannot_be_resolved() {
    when(directoryPaths.getValue()).thenReturn("${workspace_loc:/src/protos}");
    when(uris.referredResourceExists(any(URI.class))).thenReturn(false);
    String resolved = resolver.resolveUri("imported.proto", declaringResourceUri, allPreferences);
    assertNull(resolved);
  }

  @Test public void should_return_null_if_file_URI_cannot_be_resolved() {
    when(directoryPaths.getValue()).thenReturn("/usr/local/project/src/protos");
    when(uris.referredResourceExists(any(URI.class))).thenReturn(false);
    String resolved = resolver.resolveUri("imported.proto", declaringResourceUri, allPreferences);
    assertNull(resolved);
  }

  private static class TestModule extends AbstractTestModule {
    @Override protected void configure() {
      mockAndBind(Uris.class);
      mockAndBind(ResourceLocations.class);
    }
  }
}
