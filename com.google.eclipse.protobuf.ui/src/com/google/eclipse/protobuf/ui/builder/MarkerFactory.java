/*
 * Copyright (c) 2011 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.ui.builder;

import static java.lang.Integer.parseInt;
import static org.eclipse.core.resources.IMarker.*;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.runtime.CoreException;

/**
 * Parses the output of protoc and create error markers if necessary.
 *
 * @author alruiz@google.com (Alex Ruiz)
 */
class MarkerFactory {

  static final String MARKER_ID = "com.google.eclipse.protobuf.ui.pbmarker";

  /*
   * (.*):(\\d+):(\\d+):\\s*(.*)
   * --1- ---2-- ---3-- -*- --4-
   *
   * 1: file name
   * 2: line number
   * 3: column
   * *: whitespace
   * 4: description
   */
  private static final Pattern ERROR_PATTERN = Pattern.compile("(.*):(\\d+):(\\d+):\\s*(.*)");

  void parseAndCreateMarkerIfNecessary(String line, IFile file) throws CoreException {
    parseError(line, file);
  }

  private void parseError(String line, IFile file) throws CoreException {
    Matcher errorMatcher = ERROR_PATTERN.matcher(line);
    if (!errorMatcher.matches()) return;
    int lineNumber = parseInt(errorMatcher.group(2));
    String description = errorMatcher.group(4);
    IMarker marker = file.createMarker(MARKER_ID);
    marker.setAttribute(SEVERITY, SEVERITY_ERROR);
    marker.setAttribute(MESSAGE, description);
    marker.setAttribute(LINE_NUMBER, lineNumber);
  }
}
