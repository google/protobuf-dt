/*
 * Copyright (c) 2011 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.ui.builder.protoc;

import static com.google.eclipse.protobuf.junit.stubs.resources.MarkerStub.error;
import static org.eclipse.xtext.ui.MarkerTypes.FAST_VALIDATION;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;

import java.util.List;

import org.eclipse.core.runtime.*;
import org.junit.*;

import com.google.eclipse.protobuf.junit.stubs.resources.*;
import com.google.eclipse.protobuf.ui.builder.protoc.ProtocMarkerFactory;

/**
 * Tests for <code>{@link ProtocMarkerFactory#createErrorIfNecessary(String, String, int)}</code>.
 *
 * @author alruiz@google.com (Alex Ruiz)
 */
public class ProtocMarkerFactory_createErrorIfNecessary_Test {

  private static final String PROTOC = "com.google.eclipse.protobuf.ui.protocMarker";

  private FileStub file;
  private MarkerStub fastValidationMarker;
  private ProtocMarkerFactory markerFactory;

  @Before public void setUp() throws CoreException {
    file = new FileStub();
    file.setLocation(new Path("home/alex/protos/test1.proto"));
    file.createMarker(PROTOC);
    fastValidationMarker = error(FAST_VALIDATION, "Expected field name.", 68);
    file.addMarker(fastValidationMarker);
    markerFactory = new ProtocMarkerFactory(file);
  }

  @Test public void should_create_marker_if_file_paths_match_and_a_similar_marker_does_not_exist() throws CoreException {
    String message = "File not found.";
    int lineNumber = 8;
    markerFactory.createErrorIfNecessary("test1.proto", message, lineNumber);
    List<MarkerStub> markers = file.markersOfType(PROTOC);
    assertThat(markers.size(), equalTo(1));
    assertThat(markers.get(0), equalTo(error(PROTOC, message, lineNumber)));
  }

  @Test public void should_not_create_marker_if_given_path_does_not_match_path_in_file() throws CoreException {
    markerFactory.createErrorIfNecessary("test2.proto", "File not found.", 8);
    assertThat(file.markerCount(PROTOC), equalTo(0));
  }

  @Test public void should_not_create_marker_if_a_similar_one_exists() throws CoreException {
    markerFactory.createErrorIfNecessary("test1.proto", fastValidationMarker.message(),
        fastValidationMarker.lineNumber());
    assertThat(file.markerCount(PROTOC), equalTo(0));
  }
}
