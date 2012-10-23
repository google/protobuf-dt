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
 * Tests for <code>{@link RegexOutputParser#parseAndAddMarkerIfNecessary(String, ProtocMarkerFactory)}</code>.
 *
 * @author alruiz@google.com (Alex Ruiz)
 */
public class RegexOutputParser_parseAndAddMarkerIfNecessary_withoutLineNumber_Test {
  private ProtocMarkerFactory markerFactory;
  private RegexOutputParser parser;

  @Before public void setUp() {
    markerFactory = mock(ProtocMarkerFactory.class);
    parser = new RegexOutputParser("(.*):(.*)", 1, 2);
  }

  @Test public void should_not_create_IMarker_if_line_does_not_match_error_pattern() throws CoreException {
    String line = "Expected field name.";
    parser.parseAndAddMarkerIfNecessary(line, markerFactory);
    verifyZeroInteractions(markerFactory);
  }

  @Test public void should_attempt_to_create_IMarker_if_line_matches_error_pattern() throws CoreException {
    String line = "person.proto:Cannot generate Java.";
    parser.parseAndAddMarkerIfNecessary(line, markerFactory);
    verify(markerFactory).createErrorIfNecessary("person.proto", -1, "Cannot generate Java.");
  }
}
