/*
 * Copyright (c) 2011 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.ui.protoc.command;

import static java.io.File.separator;

import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;

import static com.google.common.collect.Lists.newArrayList;

import org.eclipse.xtext.util.Strings;
import org.junit.Test;

/**
 * Tests for <code>{@link CodeGenerationProtocOption#segmentsOf(String)}</code>.
 *
 * @author alruiz@google.com (Alex Ruiz)
 */
public class CodeGenerationProtocOption_segmentsOf_Test {
  @Test(expected = NullPointerException.class)
  public void should_throw_error_if_path_is_null() {
    CodeGenerationProtocOption.segmentsOf(null);
  }

  @Test public void should_separate_segments_using_system_file_separator() {
    String[] expected = { "folder1" , "folder1_1", "folder1_1_1" };
    String path = pathFrom(expected);
    String[] segments = CodeGenerationProtocOption.segmentsOf(path);
    assertThat(segments, equalTo(expected));
  }

  @Test public void should_separate_segments_for_path_ending_with_system_file_separator() {
    String[] expected = { "folder1" , "folder1_1" };
    String path = pathFrom(expected);
    String[] segments = CodeGenerationProtocOption.segmentsOf(path);
    assertThat(segments, equalTo(expected));
  }

  private String pathFrom(String[] segments) {
    return Strings.concat(separator, newArrayList(segments));
  }
}
