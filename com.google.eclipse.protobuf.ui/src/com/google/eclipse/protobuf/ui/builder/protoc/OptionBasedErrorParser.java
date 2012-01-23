/*
 * Copyright (c) 2012 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.ui.builder.protoc;

import static java.util.regex.Pattern.compile;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.core.runtime.CoreException;

/**
 * @author alruiz@google.com (Alex Ruiz)
 */
class OptionBasedErrorParser implements ProtocOutputParser {
  /*
   * (--.*):\\s*(.*):\\s*(.*)
   * --1--- -*- --2- -*- --3-
   *
   * 1: option (e.g. --java_out)
   * *: whitespace
   * 2: file name
   * *: whitespace
   * 3: description
   */
  private static final Pattern ERROR_PATTERN = compile("(--.*):\\s*(.*):\\s*(.*)");

  @Override
  public boolean parseAndAddMarkerIfNecessary(String line, ProtocMarkerFactory markerFactory) throws CoreException {
    Matcher errorMatcher = ERROR_PATTERN.matcher(line);
    if (!errorMatcher.matches()) {
      return false;
    }
    markerFactory.createErrorIfNecessary(errorMatcher.group(2), errorMatcher.group(3), -1);
    return true;
  }
}
