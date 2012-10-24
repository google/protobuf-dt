/*
 * Copyright (c) 2012 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.resource;

import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;

import static com.google.eclipse.protobuf.junit.core.UnitTestModule.unitTestModule;
import static com.google.eclipse.protobuf.junit.core.XtextRule.overrideRuntimeModuleWith;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.emf.common.util.URI;
import org.eclipse.xtext.resource.IResourceDescription;
import org.eclipse.xtext.resource.IResourceDescriptions;
import org.eclipse.xtext.resource.XtextResource;
import org.eclipse.xtext.resource.impl.ResourceSetBasedResourceDescriptions;
import org.junit.Rule;
import org.junit.Test;

import com.google.eclipse.protobuf.junit.core.XtextRule;
import com.google.inject.Inject;

/**
 * Tests for <code>{@link IndexLookup#resourceIn(IPath)}</code>.
 *
 * @author alruiz@google.com (Alex Ruiz)
 */
public class IndexLookup_resourceIn_Test {
  @Rule public XtextRule xtext = overrideRuntimeModuleWith(unitTestModule());

  @Inject private IndexLookup lookup;

  // syntax = "proto2";
  // package com.google.proto;
  //
  // message Person {}
  @Test public void should_find_resource_if_URIs_are_exact_match() {
    XtextResource resource = xtext.resource();
    addToXtextIndex(resource);
    URI resourceUri = resource.getURI();
    IPath path = Path.fromOSString(resourceUri.path());
    IResourceDescription description = lookup.resourceIn(path);
    assertThat(description.getURI(), equalTo(resourceUri));
  }

  // syntax = "proto2";
  // package com.google.proto;
  //
  // message Person {}
  @Test public void should_find_resource_matching_segments_if_URIs_are_not_exact_match() {
    XtextResource resource = xtext.resource();
    addToXtextIndex(resource);
    URI resourceUri = resource.getURI();
    String[] segments = resourceUri.segments();
    int segmentCount = segments.length;
    String path = segments[segmentCount - 2] + "/" + segments[segmentCount - 1]; // last two segments.
    IResourceDescription description = lookup.resourceIn(Path.fromOSString(path));
    assertThat(description.getURI(), equalTo(resourceUri));
  }

  // syntax = "proto2";
  // package com.google.proto;
  //
  // message Person {}
  @Test public void should_return_null_if_matching_URI_was_not_found() {
    XtextResource resource = xtext.resource();
    addToXtextIndex(resource);
    IResourceDescription description = lookup.resourceIn(Path.fromOSString("some/crazy/path"));
    assertNull(description);
  }

  private void addToXtextIndex(XtextResource resource) {
    IResourceDescriptions xtextIndex = lookup.getXtextIndex();
    if (xtextIndex instanceof ResourceSetBasedResourceDescriptions) {
      ((ResourceSetBasedResourceDescriptions) xtextIndex).setContext(resource);
    }
  }
}
