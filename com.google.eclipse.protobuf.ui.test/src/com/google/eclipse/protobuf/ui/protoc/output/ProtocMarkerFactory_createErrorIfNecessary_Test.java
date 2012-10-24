/*
 * Copyright (c) 2011 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.ui.protoc.output;

import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;

import static com.google.eclipse.protobuf.junit.stubs.resources.MarkerStub.error;
import static com.google.eclipse.protobuf.ui.validation.MarkerTypes.EDITOR_CHECK;

import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Path;
import org.junit.Before;
import org.junit.Test;

import com.google.eclipse.protobuf.junit.stubs.resources.FileStub;
import com.google.eclipse.protobuf.junit.stubs.resources.MarkerStub;

/**
 * Tests for <code>{@link ProtocMarkerFactory#createErrorIfNecessary(String, int, String)}</code>.
 *
 * @author alruiz@google.com (Alex Ruiz)
 */
public class ProtocMarkerFactory_createErrorIfNecessary_Test {
  private static final String PROTOC = "com.google.eclipse.protobuf.ui.protocMarker";

  private MarkerStub marker;
  private FileStub file;
  private ProtocMarkerFactory markerFactory;

  @Before public void setUp() throws CoreException {
    marker = error(EDITOR_CHECK, "Expected field name.", 68);
    file = new FileStub();
    file.setLocation(Path.fromOSString("home/alex/protos/test1.proto"));
    file.createMarker(PROTOC);
    file.addMarker(marker);
    markerFactory = new ProtocMarkerFactory(file);
  }

  @Test public void should_create_marker_if_file_paths_match_and_a_similar_marker_does_not_exist() throws CoreException {
    String message = "File not found.";
    int lineNumber = 8;
    markerFactory.createErrorIfNecessary("test1.proto", lineNumber, message);
    List<MarkerStub> markers = file.markersOfType(PROTOC);
    assertThat(markers.size(), equalTo(1));
    assertThat(markers.get(0), equalTo(error(PROTOC, message, lineNumber)));
  }

  @Test public void should_not_create_marker_if_given_path_does_not_match_path_in_file() throws CoreException {
    markerFactory.createErrorIfNecessary("test2.proto", 8, "File not found.");
    assertThat(file.markerCount(PROTOC), equalTo(0));
  }

  @Test public void should_not_create_marker_if_a_similar_one_exists() throws CoreException {
    markerFactory.createErrorIfNecessary("test1.proto", marker.lineNumber(), marker.message());
    assertThat(file.markerCount(PROTOC), equalTo(0));
  }
}
