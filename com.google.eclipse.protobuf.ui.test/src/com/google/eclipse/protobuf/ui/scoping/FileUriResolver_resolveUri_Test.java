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

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.junit.*;

import com.google.eclipse.protobuf.junit.core.XtextRule;
import com.google.eclipse.protobuf.scoping.IFileUriResolver;
import com.google.inject.*;

/**
 * Tests for <code>{@link FileUriResolver#resolveUri(String, Resource)}</code>.
 *
 * @author alruiz@google.com (Alex Ruiz)
 */
public class FileUriResolver_resolveUri_Test {

  @Rule public XtextRule xtext = new XtextRule();
  
  private URI resourceUri;
  private ResourcesStub resources;
  private FileUriResolver resolver;

  @Before public void setUp() {
    resources = new ResourcesStub();
    resources.resourceShouldAlwaysExist = true;
    Module module = new Module() {
      public void configure(Binder binder) {
        binder.bind(Resources.class).toInstance(resources);
        binder.bind(IFileUriResolver.class).to(FileUriResolver.class);
      }
    };
    Injector injector = xtext.injector().createChildInjector(module);
    resourceUri = createURI("platform:/resource/src/proto/person.proto");
    resolver = (FileUriResolver) injector.getInstance(IFileUriResolver.class);
  }

  @Test public void should_resolve_import_URI_if_missing_scheme() {
    resolver.resolveUri("folder1/address.proto", null);
//    String uri = resolver.resolveUri("folder1/address.proto", resourceUri);
//    assertThat(uri, equalTo("platform:/resource/src/proto/folder1/address.proto"));  
  }
//
//  @Test public void should_not_resolve_import_URI_if_not_missing_scheme() {
//    String originalUri = "platform:/resource/src/proto/folder1/address.proto";
//    String uri = resolver.resolveUri(originalUri, resourceUri, resources);
//    assertThat(uri, equalTo(originalUri));
//  }
//  
//  @Test public void should_resolve_import_URI_even_if_overlapping_folders_with_resource_URI() {
//    String uri = resolver.resolveUri("src/proto/folder1/address.proto", resourceUri);
//    assertThat(uri, equalTo("platform:/resource/src/proto/folder1/address.proto"));  
//  }
//
//  @Test public void should_resolve_import_URI_even_if_overlapping_one_folder_only_with_resource_URI() {
//    String uri = resolver.resolveUri("src/proto/read-only/address.proto", resourceUri);
//    assertThat(uri, equalTo("platform:/resource/src/proto/read-only/address.proto"));  
//  }
//  
  private static class ResourcesStub extends Resources {
    boolean resourceShouldAlwaysExist;
    
    @Override public boolean fileExists(URI uri) {
      return resourceShouldAlwaysExist;
    }
  }
}
