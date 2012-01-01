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
import static org.eclipse.core.resources.IResource.DEPTH_ZERO;
import static org.eclipse.xtext.ui.MarkerTypes.*;
import static org.mockito.Mockito.*;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.*;
import org.eclipse.xtext.validation.CheckMode;
import org.junit.*;

/**
 * Tests for <code>{@link ProtobufResourceUIValidatorExtension#deleteMarkers(IFile, CheckMode, IProgressMonitor)}</code>.
 *
 * @author alruiz@google.com (Alex Ruiz)
 */
public class ProtobufResourceUIValidatorExtension_deleteMarkers_Test {
  private static CheckMode checkMode;
  private static IProgressMonitor monitor;

  @BeforeClass public static void setUpOnce() {
    checkMode = mock(CheckMode.class);
    monitor = mock(IProgressMonitor.class);
  }

  private IFile file;
  private ProtobufResourceUIValidatorExtension validator;

  @Before public void setUp() {
    file = mock(IFile.class);
    validator = new ProtobufResourceUIValidatorExtension();
  }

  @Test public void should_delete_all_xtext_and_protocol_buffer_editor_markers() throws CoreException {
    validator.deleteMarkers(file, checkMode, monitor);
    verifyDeletionOfMarkers(FAST_VALIDATION, NORMAL_VALIDATION, EDITOR_CHECK);
  }

  private void verifyDeletionOfMarkers(String...markerTypes) throws CoreException {
    for (String markerType : markerTypes) {
      verify(file).deleteMarkers(markerType, true, DEPTH_ZERO);
    }
  }
}
