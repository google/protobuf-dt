/*
 * Copyright (c) 2011 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.ui.validation;

import static org.eclipse.core.resources.IResource.DEPTH_ZERO;

import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.*;
import org.eclipse.xtext.ui.editor.validation.MarkerCreator;
import org.eclipse.xtext.ui.validation.DefaultResourceUIValidatorExtension;
import org.eclipse.xtext.validation.*;

import com.google.common.annotations.VisibleForTesting;
import com.google.inject.Inject;

/**
 * Creates/deletes markers of type "Protocol Buffer Problem" instead of the default "Xtext Check."
 *
 * @author alruiz@google.com (Alex Ruiz)
 */
public class ProtobufResourceUIValidatorExtension extends DefaultResourceUIValidatorExtension {

  public static final String EDITOR_CHECK = "com.google.eclipse.protobuf.ui.editorMarker";

  @VisibleForTesting
  @Inject MarkerCreator markerCreator;

  @Override protected void createMarkers(IFile file, List<Issue> list, IProgressMonitor monitor) throws CoreException {
    for (Issue issue : list) {
      markerCreator.createMarker(issue, file, EDITOR_CHECK);
    }
  }

  @Override protected void deleteMarkers(IFile file, CheckMode checkMode, IProgressMonitor monitor)
      throws CoreException {
    super.deleteMarkers(file, checkMode, monitor);
    file.deleteMarkers(EDITOR_CHECK, true, DEPTH_ZERO);
  }
}
