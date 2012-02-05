/*
 * Copyright (c) 2012 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.util;

import static org.junit.Assert.*;

import org.eclipse.core.runtime.*;
import org.eclipse.emf.common.util.URI;
import org.junit.*;

/**
 * Tests for <code>{@link IPaths#areReferringToSameFile(IPath, URI)}</code>
 *
 * @author alruiz@google.com (Alex Ruiz)
 */
public class IPaths_areReferringToSameFile_Tests {
  private IPaths paths;

  @Before public void setUp() {
    paths = new IPaths();
  }

  @Test public void should_return_true_if_both_have_exactly_equal_segments() {
    String pathValue = "/usr/local/google/proto";
    IPath path = new Path(pathValue);
    URI uri = URI.createPlatformResourceURI(pathValue, false);
    assertTrue(paths.areReferringToSameFile(path, uri));
  }

  @Test public void should_return_true_if_path_is_subset_of_URI() {
    IPath path = new Path("/google/proto");
    URI uri = URI.createPlatformResourceURI("/usr/local/google/proto", false);
    assertTrue(paths.areReferringToSameFile(path, uri));
  }

  @Test public void should_return_false_if_last_segments_in_path_and_URI_are_not_equal() {
    IPath path = new Path("/usr/local/google/proto");
    URI uri = URI.createPlatformResourceURI("/usr/local/google/cpp", false);
    assertFalse(paths.areReferringToSameFile(path, uri));
  }
}
