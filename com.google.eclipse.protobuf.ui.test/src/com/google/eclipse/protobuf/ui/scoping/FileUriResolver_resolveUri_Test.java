/*
 * Copyright (c) 2011 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.ui.scoping;

import static com.google.eclipse.protobuf.ui.preferences.paths.FileResolutionType.SINGLE_FOLDER;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

import org.eclipse.core.resources.IProject;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.junit.*;

import com.google.eclipse.protobuf.junit.core.XtextRule;
import com.google.eclipse.protobuf.junit.stubs.ResourceStub;
import com.google.eclipse.protobuf.scoping.IFileUriResolver;
import com.google.eclipse.protobuf.ui.preferences.paths.*;
import com.google.inject.*;

/**
 * Tests for <code>{@link FileUriResolver#resolveUri(String, Resource)}</code>.
 *
 * @author alruiz@google.com (Alex Ruiz)
 */
public class FileUriResolver_resolveUri_Test {

  @Rule public XtextRule xtext = new XtextRule();
  
  private Resource resource;
  private PreferenceReader preferenceReader;
  private Preferences preferences;
  private IProject project;
  private Resources resources;

  private FileUriResolver resolver;

  @Before public void setUp() {
    resource = new ResourceStub("platform:/resource/src/proto/person.proto");
    preferenceReader = mock(PreferenceReader.class);
    preferences = mock(Preferences.class);
    project = mock(IProject.class);
    resources = mock(Resources.class);
    Module module = new Module() {
      public void configure(Binder binder) {
        binder.bind(PreferenceReader.class).toInstance(preferenceReader);
        binder.bind(Resources.class).toInstance(resources);
        binder.bind(IFileUriResolver.class).to(FileUriResolver.class);
      }
    };
    Injector injector = xtext.injector().createChildInjector(module);
    resolver = (FileUriResolver) injector.getInstance(IFileUriResolver.class);
  }

  @Test public void should_resolve_import_URI_if_missing_scheme() {
    callStubs(SINGLE_FOLDER, true);
    String resolved = resolver.resolveUri("folder1/address.proto", resource);
    assertThat(resolved, equalTo("platform:/resource/src/proto/folder1/address.proto"));  
  }
  
  @Test public void should_not_resolve_import_URI_if_not_missing_scheme() {
    callStubs(SINGLE_FOLDER, true);
    String original = "platform:/resource/src/proto/folder1/address.proto";
    String resolved = resolver.resolveUri(original, resource);
    assertThat(resolved, equalTo(original));
  }
  
  @Test public void should_resolve_import_URI_even_if_overlapping_folders_with_resource_URI() {
    callStubs(SINGLE_FOLDER, true);
    String resolved = resolver.resolveUri("src/proto/folder1/address.proto", resource);
    assertThat(resolved, equalTo("platform:/resource/src/proto/folder1/address.proto"));  
  }

  @Test public void should_resolve_import_URI_even_if_overlapping_one_folder_only_with_resource_URI() {
    callStubs(SINGLE_FOLDER, true);
    String resolved = resolver.resolveUri("src/proto/read-only/address.proto", resource);
    assertThat(resolved, equalTo("platform:/resource/src/proto/read-only/address.proto"));  
  }

  private void callStubs(FileResolutionType type, boolean resolvedUriExists) {
    when(resources.project(resource)).thenReturn(project);
    when(preferenceReader.readFromPrefereceStore(project)).thenReturn(preferences);
    when(preferences.fileResolutionType()).thenReturn(type);
    when(resources.fileExists(any(URI.class))).thenReturn(resolvedUriExists);
  }
}
