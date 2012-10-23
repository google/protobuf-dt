/*
 * Copyright (c) 2012 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.ui.protoc.output;

import static java.lang.Integer.parseInt;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.core.runtime.CoreException;

/**
 * @author alruiz@google.com (Alex Ruiz)
 */
class RegexOutputParser implements ProtocOutputParser {
  private final Pattern pattern;
  private final int fileNameGroup;
  private final int lineNumberGroup;
  private final int messageGroup;

  RegexOutputParser(String regex, int fileNameGroup, int messageGroup) {
    this(regex, fileNameGroup, 0, messageGroup);
  }

  RegexOutputParser(String regex, int fileNameGroup, int lineNumberGroup, int messageGroup) {
    this.fileNameGroup = fileNameGroup;
    this.lineNumberGroup = lineNumberGroup;
    this.messageGroup = messageGroup;
    this.pattern = Pattern.compile(regex);
  }

  @Override public boolean parseAndAddMarkerIfNecessary(String line, ProtocMarkerFactory markerFactory)
      throws CoreException {
    Matcher matcher = pattern.matcher(line);
    if (!(matcher.matches())) {
      return false;
    }
    String fileName = matcher.group(fileNameGroup);
    int lineNumber = -1;
    if (lineNumberGroup > 0) {
      lineNumber = parseInt(matcher.group(lineNumberGroup));
    }
    String message = matcher.group(messageGroup);
    markerFactory.createErrorIfNecessary(fileName, lineNumber, message);
    return true;
  }
}
