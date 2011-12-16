/*
 * Copyright (c) 2011 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.ui.util;

import static java.io.File.separator;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;

import org.junit.Test;

/**
 * Tests for <code>{@link Paths#segmentsOf(String)}</code>.
 *
 * @author alruiz@google.com (Alex Ruiz)
 */
public class Paths_segmentsOf_Test {

  @Test(expected = NullPointerException.class)
  public void should_throw_error_if_path_is_null() {
    Paths.segmentsOf(null);
  }

  @Test public void should_separate_segments_using_system_file_separator() {
    String path = "folder1" + separator + "folder1_1" + separator + "folder1_1_1";
    assertThat(Paths.segmentsOf(path), equalTo(new String[] { "folder1" , "folder1_1", "folder1_1_1" }));
  }

  @Test public void should_separate_segments_for_path_ending_with_system_file_separator() {
    String path = "folder1" + separator + "folder1_1" + separator;
    assertThat(Paths.segmentsOf(path), equalTo(new String[] { "folder1" , "folder1_1" }));
  }
}
