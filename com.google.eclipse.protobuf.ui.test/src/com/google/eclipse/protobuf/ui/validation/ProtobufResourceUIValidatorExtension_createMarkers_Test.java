/*
 * Copyright (c) 2011 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.ui.validation;

import static com.google.eclipse.protobuf.ui.validation.ProtobufResourceUIValidatorExtension.EDITOR_CHECK;
import static java.util.Arrays.asList;
import static org.mockito.Mockito.*;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.*;
import org.eclipse.xtext.ui.editor.validation.MarkerCreator;
import org.eclipse.xtext.validation.Issue;
import org.junit.*;

import java.util.List;

/**
 * Tests for <code>{@link ProtobufResourceUIValidatorExtension#createMarkers(IFile, List, IProgressMonitor)}</code>
 * 
 * @author alruiz@google.com (Alex Ruiz)
 */
public class ProtobufResourceUIValidatorExtension_createMarkers_Test {

  private static IFile file;
  private static List<Issue> issues;
  private static IProgressMonitor monitor;

  @BeforeClass public static void setUpOnce() {
    file = mock(IFile.class);
    issues = asList(mock(Issue.class), mock(Issue.class));
    monitor = mock(IProgressMonitor.class);
  }

  private MarkerCreator markerCreator;
  private ProtobufResourceUIValidatorExtension validator;
  
  @Before public void setUp() {
    markerCreator = mock(MarkerCreator.class);
    validator = new ProtobufResourceUIValidatorExtension();
    validator.markerCreator = markerCreator;
  }
  
  @Test public void should_create_markers_using_proto_editor_marker_type() throws CoreException {
    validator.createMarkers(file, issues, monitor);
    for (Issue issue : issues) {
      verify(markerCreator).createMarker(issue, file, EDITOR_CHECK);
    }
  }
}
