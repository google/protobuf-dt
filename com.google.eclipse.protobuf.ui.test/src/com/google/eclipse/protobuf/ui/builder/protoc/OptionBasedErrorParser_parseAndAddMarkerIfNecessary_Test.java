/*
 * Copyright (c) 2011 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.ui.builder.protoc;

import static org.mockito.Mockito.*;

import org.eclipse.core.runtime.CoreException;
import org.junit.Before;
import org.junit.Test;

/**
 * Tests for <code>{@link OptionBasedErrorParser#parseAndAddMarkerIfNecessary(String, ProtocMarkerFactory)}</code>.
 *
 * @author alruiz@google.com (Alex Ruiz)
 */
public class OptionBasedErrorParser_parseAndAddMarkerIfNecessary_Test {
  private ProtocMarkerFactory markerFactory;
  private OptionBasedErrorParser outputParser;

  @Before public void setUp() {
    markerFactory = mock(ProtocMarkerFactory.class);
    outputParser = new OptionBasedErrorParser();
  }

  @Test public void should_not_create_IMarker_if_line_does_not_match_error_pattern() throws CoreException {
    String line = "Expected field name.";
    outputParser.parseAndAddMarkerIfNecessary(line, markerFactory);
    verifyZeroInteractions(markerFactory);
  }

  @Test public void should_attempt_to_create_IMarker_if_line_matches_error_pattern() throws CoreException {
    String line = "--java_out: geocoding.proto: geocoding.proto: Cannot generate Java output.";
    outputParser.parseAndAddMarkerIfNecessary(line, markerFactory);
    verify(markerFactory).createErrorIfNecessary("geocoding.proto", "Cannot generate Java output.", -1);
  }
}
