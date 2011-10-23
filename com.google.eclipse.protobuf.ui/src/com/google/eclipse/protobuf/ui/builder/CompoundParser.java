// Copyright 2011 Google Inc. All Rights Reserved.

package com.google.eclipse.protobuf.ui.builder;

import static java.util.Arrays.asList;

import org.eclipse.core.runtime.CoreException;

import java.util.*;

/**
 * @author alruiz@google.com (Alex Ruiz)
 */
class CompoundParser implements ProtocOutputParser {

  private static final List<ProtocOutputParser> PARSERS = asList(new LineSpecificErrorParser(), new CodeGenerationErrorParser()); 
  
  @Override 
  public boolean parseAndAddMarkerIfNecessary(String line, ProtocMarkerFactory markerFactory) throws CoreException {
    for (ProtocOutputParser parser: PARSERS)
      if (parser.parseAndAddMarkerIfNecessary(line, markerFactory)) return true;
    return false;
  }
}
