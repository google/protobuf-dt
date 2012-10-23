/*
 * Copyright (c) 2011 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.ui.protoc.output;

import static org.eclipse.core.resources.IMarker.LINE_NUMBER;
import static org.eclipse.core.resources.IMarker.MESSAGE;
import static org.eclipse.core.resources.IMarker.SEVERITY;
import static org.eclipse.core.resources.IMarker.SEVERITY_ERROR;
import static org.eclipse.core.resources.IResource.DEPTH_INFINITE;

import static com.google.eclipse.protobuf.ui.validation.MarkerTypes.EDITOR_CHECK;
import static com.google.eclipse.protobuf.ui.validation.MarkerTypes.PROTOC_CHECK;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.runtime.CoreException;

/**
 * Factory of <code>{@link IMarker}</code>s derived from errors reported by protoc.
 *
 * @author alruiz@google.com (Alex Ruiz)
 */
public class ProtocMarkerFactory {
  private final IFile protoFile;
  private final IMarker[] markers;

  public ProtocMarkerFactory(IFile protoFile) throws CoreException {
    this.protoFile = protoFile;
    protoFile.deleteMarkers(PROTOC_CHECK, true, DEPTH_INFINITE);
    markers = protoFile.findMarkers(EDITOR_CHECK, true, DEPTH_INFINITE);
  }

  /**
   * Creates a new editor marker if the given file name matches the one in this factory.
   * @param fileName the name of the proto file, obtained from protoc output.
   * @param lineNumber the line number where to create the editor marker.
   * @param message the message for the editor marker.
   * @throws CoreException if something goes wrong.
   */
  public void createErrorIfNecessary(String fileName, int lineNumber, String message) throws CoreException {
    String location = protoFile.getLocation().toOSString();
    if (!location.endsWith(fileName) || containsMarker(message, lineNumber)) {
      return;
    }
    IMarker marker = protoFile.createMarker(PROTOC_CHECK);
    marker.setAttribute(SEVERITY, SEVERITY_ERROR);
    marker.setAttribute(MESSAGE, message);
    marker.setAttribute(LINE_NUMBER, lineNumber);
  }

  private boolean containsMarker(String description, int lineNumber) throws CoreException {
    for (IMarker marker : markers) {
      String markerMessage = (String) marker.getAttribute(MESSAGE);
      if (markerMessage.equalsIgnoreCase(description) && marker.getAttribute(LINE_NUMBER).equals(lineNumber)) {
        return true;
      }
    }
    return false;
  }
}
