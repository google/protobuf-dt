/*
 * Copyright (c) 2012 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.resource;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import static com.google.eclipse.protobuf.junit.core.UnitTestModule.unitTestModule;
import static com.google.eclipse.protobuf.junit.core.XtextRule.overrideRuntimeModuleWith;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.emf.common.util.URI;
import org.junit.Rule;
import org.junit.Test;

import com.google.eclipse.protobuf.junit.core.XtextRule;
import com.google.inject.Inject;

/**
 * Tests for <code>{@link IndexLookup#areReferringToSameFile(IPath, URI)}</code>
 *
 * @author alruiz@google.com (Alex Ruiz)
 */
public class IndexLookup_areReferringToSameFile_Tests {
  @Rule public XtextRule xtext = overrideRuntimeModuleWith(unitTestModule());

  @Inject private IndexLookup lookup;

  @Test public void should_return_true_if_both_have_exactly_equal_segments() {
    String pathValue = "/usr/local/google/proto";
    IPath path = Path.fromOSString(pathValue);
    URI uri = URI.createPlatformResourceURI(pathValue, false);
    assertTrue(lookup.areReferringToSameFile(path, uri));
  }

  @Test public void should_return_true_if_path_is_subset_of_URI() {
    IPath path = Path.fromOSString("/google/proto");
    URI uri = URI.createPlatformResourceURI("/usr/local/google/proto", true);
    assertTrue(lookup.areReferringToSameFile(path, uri));
  }

  @Test public void should_return_false_if_last_segments_in_path_and_URI_are_not_equal() {
    IPath path = Path.fromOSString("/usr/local/google/proto");
    URI uri = URI.createPlatformResourceURI("/usr/local/google/cpp", true);
    assertFalse(lookup.areReferringToSameFile(path, uri));
  }
}
