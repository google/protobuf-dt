/*
 * Copyright (c) 2011 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.scoping;

import static org.eclipse.emf.common.util.URI.createURI;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;

import org.eclipse.emf.common.util.URI;
import org.junit.Before;
import org.junit.Test;

/**
 * Tests for <code>{@link ImportUriFixer#fixUri(String, URI, ResourceChecker)}</code>.
 *
 * @author alruiz@google.com (Alex Ruiz)
 */
public class ImportUriFixer_fixUri_Test {

  private URI resourceUri;
  private ResourceCheckerStub resourceChecker;
  private ImportUriFixer fixer;

  @Before public void setUp() {
    resourceUri = createURI("platform:/resource/src/proto/person.proto");
    resourceChecker = new ResourceCheckerStub();
    resourceChecker.resourceShouldAlwaysExist = true;
    fixer = new ImportUriFixer();
  }

  @Test public void should_fix_import_URI_if_missing_scheme() {
    String uri = fixer.fixUri("folder1/address.proto", resourceUri, resourceChecker);
    assertThat(uri, equalTo("platform:/resource/src/proto/folder1/address.proto"));  
  }

  @Test public void should_not_fix_import_URI_if_not_missing_scheme() {
    String originalUri = "platform:/resource/src/proto/folder1/address.proto";
    String uri = fixer.fixUri(originalUri, resourceUri, resourceChecker);
    assertThat(uri, equalTo(originalUri));
  }
  
  @Test public void should_fix_import_URI_even_if_overlapping_folders_with_resource_URI() {
    String uri = fixer.fixUri("src/proto/folder1/address.proto", resourceUri, resourceChecker);
    assertThat(uri, equalTo("platform:/resource/src/proto/folder1/address.proto"));  
  }

  @Test public void should_fix_import_URI_even_if_overlapping_one_folder_only_with_resource_URI() {
    String uri = fixer.fixUri("src/proto/read-only/address.proto", resourceUri, resourceChecker);
    assertThat(uri, equalTo("platform:/resource/src/proto/read-only/address.proto"));  
  }
  
  private static class ResourceCheckerStub extends ResourceChecker {
    boolean resourceShouldAlwaysExist;
    
    ResourceCheckerStub() {
      super(null);
    }

    @Override boolean resourceExists(String uri) {
      return resourceShouldAlwaysExist;
    }
  }
}
