/*
 * Copyright (c) 2011 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.ui.builder.protoc;

import static java.util.regex.Pattern.compile;

import java.util.regex.*;

import org.eclipse.core.runtime.CoreException;

/**
 * @author alruiz@google.com (Alex Ruiz)
 */
class CodeGenerationErrorParser implements ProtocOutputParser {

  /*
   * (.*):\\s*(--.*)
   * --1- -*- --2-
   *
   * 1: file name
   * *: whitespace
   * 2: description
   */
  private static final Pattern ERROR_PATTERN = compile("(.*):\\s*(--.*)");

  @Override
  public boolean parseAndAddMarkerIfNecessary(String line, ProtocMarkerFactory markerFactory) throws CoreException {
    Matcher errorMatcher = ERROR_PATTERN.matcher(line);
    if (!errorMatcher.matches()) return false;
    markerFactory.createErrorIfNecessary(errorMatcher.group(1), errorMatcher.group(2), -1);
    return true;
  }
}
