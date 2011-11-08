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
import org.junit.*;

/**
 * Tests for <code>{@link CodeGenerationErrorParser#parseAndAddMarkerIfNecessary(String, ProtocMarkerFactory)}</code>.
 *
 * @author alruiz@google.com (Alex Ruiz)
 */
public class CodeGenerationErrorParser_parseAndAddMarkerIfNecessary_Test {

  private ProtocMarkerFactory markerFactory;
  private CodeGenerationErrorParser outputParser;

  @Before public void setUp() {
    markerFactory = mock(ProtocMarkerFactory.class);
    outputParser = new CodeGenerationErrorParser();
  }

  @Test public void should_not_create_IMarker_if_line_does_not_match_error_pattern() throws CoreException {
    String line = "Expected field name.";
    outputParser.parseAndAddMarkerIfNecessary(line, markerFactory);
    verifyZeroInteractions(markerFactory);
  }

  @Test public void should_attempt_to_create_IMarker_if_line_matches_error_pattern() throws CoreException {
    String line = "person.proto: --java_out: person.proto: Cannot generate Java.";
    outputParser.parseAndAddMarkerIfNecessary(line, markerFactory);
    verify(markerFactory).createErrorIfNecessary("person.proto", "--java_out: person.proto: Cannot generate Java.", -1);
  }
}
