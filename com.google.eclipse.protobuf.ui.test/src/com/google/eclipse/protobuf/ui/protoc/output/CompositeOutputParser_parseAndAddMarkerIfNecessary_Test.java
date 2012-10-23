/*
 * Copyright (c) 2011 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.ui.protoc.output;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;

import org.eclipse.core.runtime.CoreException;
import org.junit.Before;
import org.junit.Test;

/**
 * Tests for <code>{@link CompositeOutputParser#parseAndAddMarkerIfNecessary(String, ProtocMarkerFactory)}</code>.
 *
 * @author alruiz@google.com (Alex Ruiz)
 */
public class CompositeOutputParser_parseAndAddMarkerIfNecessary_Test {
  private ProtocMarkerFactory markerFactory;
  private CompositeOutputParser parser;

  @Before public void setUp() {
    markerFactory = mock(ProtocMarkerFactory.class);
    parser = new CompositeOutputParser();
  }

  @Test public void should_not_create_IMarker_if_line_does_not_match_error_pattern() throws CoreException {
    String line = "person.proto: File not found.";
    parser.parseAndAddMarkerIfNecessary(line, markerFactory);
    verifyZeroInteractions(markerFactory);
  }

  @Test public void should_attempt_to_create_IMarker_if_parser1_can_parse_line() throws CoreException {
    String line = "test.proto:23:21: Expected field name.";
    parser.parseAndAddMarkerIfNecessary(line, markerFactory);
    verify(markerFactory).createErrorIfNecessary("test.proto", 23, "Expected field name.");
  }

  @Test public void should_attempt_to_create_IMarker_if_parser2_can_parse_line() throws CoreException {
    String line = "person.proto: --java_out: person.proto: Cannot generate Java.";
    parser.parseAndAddMarkerIfNecessary(line, markerFactory);
    verify(markerFactory).createErrorIfNecessary("person.proto", -1, "--java_out: person.proto: Cannot generate Java.");
  }

  @Test public void should_attempt_to_create_IMarker_if_parser3_can_parse_line() throws CoreException {
    String line = "--java_out: geocoding.proto: geocoding.proto: Cannot generate Java output.";
    parser.parseAndAddMarkerIfNecessary(line, markerFactory);
    verify(markerFactory).createErrorIfNecessary("geocoding.proto", -1, "Cannot generate Java output.");
  }
}
