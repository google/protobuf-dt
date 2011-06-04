/*
 * Copyright (c) 2011 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.ui.builder;

import static com.google.eclipse.protobuf.junit.stubs.resources.MarkerStub.error;
import static org.eclipse.xtext.ui.MarkerTypes.FAST_VALIDATION;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;

import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.junit.Before;
import org.junit.Test;

import com.google.eclipse.protobuf.junit.stubs.resources.*;

/**
 * Tests for <code>{@link ProtocMarkerFactory#createErrorIfNecessary(String, int)}</code>.
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
    file.createMarker(PROTOC);
    fastValidationMarker = error(FAST_VALIDATION, "Expected field name.", 68);
    file.addMarker(fastValidationMarker);
    markerFactory = new ProtocMarkerFactory(file);
  }
  
  @Test public void should_create_marker_if_a_similar_one_does_not_exist() throws CoreException {
    String message = "File not found.";
    int lineNumber = 8;
    markerFactory.createErrorIfNecessary(message, lineNumber);
    List<MarkerStub> markers = file.markers(PROTOC);
    assertThat(markers.size(), equalTo(1));
    assertThat(markers.get(0), equalTo(error(PROTOC, message, lineNumber)));
  }
  
  @Test public void should_not_create_marker_if_a_similar_one_exists() throws CoreException {
    markerFactory.createErrorIfNecessary(fastValidationMarker.message(), fastValidationMarker.lineNumber());
    assertThat(file.markerCount(PROTOC), equalTo(0));
  }
}
