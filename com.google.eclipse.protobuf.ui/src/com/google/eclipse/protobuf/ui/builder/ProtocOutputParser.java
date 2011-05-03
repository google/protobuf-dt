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

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.core.runtime.CoreException;

/**
 * Parses the output of protoc.
 *
 * @author alruiz@google.com (Alex Ruiz)
 */
class ProtocOutputParser {

  static final String MARKER_ID = "com.google.eclipse.protobuf.ui.protocMarker";

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

  void parseAndAddMarkerIfNecessary(String line, ProtocMarkerFactory markerFactory) throws CoreException {
    Matcher errorMatcher = ERROR_PATTERN.matcher(line);
    if (!errorMatcher.matches()) return;
    int lineNumber = parseInt(errorMatcher.group(2));
    String description = errorMatcher.group(4);
    markerFactory.createErrorIfNecessary(description, lineNumber);
  }
}
