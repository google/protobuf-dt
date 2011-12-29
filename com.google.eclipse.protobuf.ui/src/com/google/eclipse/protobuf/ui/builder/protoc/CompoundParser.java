/*
 * Copyright (c) 2011 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.ui.builder.protoc;

import static java.util.Arrays.asList;

import java.util.List;

import org.eclipse.core.runtime.CoreException;

/**
 * @author alruiz@google.com (Alex Ruiz)
 */
class CompoundParser implements ProtocOutputParser {
  private static final List<ProtocOutputParser> PARSERS =
      asList(new LineSpecificErrorParser(), new CodeGenerationErrorParser());

  @Override
  public boolean parseAndAddMarkerIfNecessary(String line, ProtocMarkerFactory markerFactory) throws CoreException {
    for (ProtocOutputParser parser: PARSERS) {
      if (parser.parseAndAddMarkerIfNecessary(line, markerFactory)) {
        return true;
      }
    }
    return false;
  }
}
