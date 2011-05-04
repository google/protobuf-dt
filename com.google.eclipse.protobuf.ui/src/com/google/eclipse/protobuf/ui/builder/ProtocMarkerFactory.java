/*
 * Copyright (c) 2011 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.ui.builder;

import static org.eclipse.core.resources.IMarker.*;
import static org.eclipse.core.resources.IResource.DEPTH_INFINITE;
import static org.eclipse.xtext.ui.MarkerTypes.FAST_VALIDATION;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.runtime.CoreException;

/**
 * Factory of <code>{@link IMarker}</code>s derived from errors reported by protoc.
 *
 * @author alruiz@google.com (Alex Ruiz)
 */
class ProtocMarkerFactory {

  private static final String PROTOC = "com.google.eclipse.protobuf.ui.protocMarker";

  private final IFile file;
  private final IMarker[] markers;

  ProtocMarkerFactory(IFile file) throws CoreException {
    this.file = file;
    file.deleteMarkers(PROTOC, true, DEPTH_INFINITE);
    markers = file.findMarkers(FAST_VALIDATION, true, DEPTH_INFINITE);
  }
  
  void createErrorIfNecessary(String message, int lineNumber) throws CoreException {
    if (containsMarker(message, lineNumber)) return;
    IMarker marker = file.createMarker(PROTOC);
    marker.setAttribute(SEVERITY, SEVERITY_ERROR);
    marker.setAttribute(MESSAGE, message);
    marker.setAttribute(LINE_NUMBER, lineNumber);
  }
  
  private boolean containsMarker(String description, int lineNumber) throws CoreException {
    for (IMarker marker : markers)
      if (marker.getAttribute(MESSAGE).equals(description) && marker.getAttribute(LINE_NUMBER).equals(lineNumber))
        return true;
    return false;
  }
}
