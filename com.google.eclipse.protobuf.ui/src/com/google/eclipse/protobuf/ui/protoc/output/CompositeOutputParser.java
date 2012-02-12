/*
 * Copyright (c) 2011 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.ui.protoc.output;

import static com.google.common.collect.ImmutableList.of;

import org.eclipse.core.runtime.CoreException;

import com.google.common.collect.ImmutableList;

/**
 * @author alruiz@google.com (Alex Ruiz)
 */
class CompositeOutputParser implements ProtocOutputParser {
  /*
   * (.*):(\\d+):(\\d+):\\s*(.*)
   * --1- ---2-- ---3-- -*- --4-
   *
   * 1: file name
   * 2: line number
   * 3: column
   * *: whitespace
   * 4: message
   */
  private static final ProtocOutputParser PARSER1 = new RegexOutputParser("(.*):(\\d+):(\\d+):\\s*(.*)", 1, 2, 4);

  /*
   * (.*):\\s*(--.*)
   * --1- -*- --2-
   *
   * 1: file name
   * *: whitespace
   * 2: message
   */
  private static final ProtocOutputParser PARSER2 = new RegexOutputParser("(.*):\\s*(--.*)", 1, 2);

  /*
   * (--.*):\\s*(.*):\\s*(.*)
   * --1--- -*- --2- -*- --3-
   *
   * 1: option (e.g. --java_out)
   * *: whitespace
   * 2: file name
   * *: whitespace
   * 3: message
   */
  private static final ProtocOutputParser PARSER3 = new RegexOutputParser("(--.*):\\s*(.*):\\s*(.*)", 2, 3);

  private static final ImmutableList<ProtocOutputParser> PARSERS = of(PARSER1, PARSER2, PARSER3);

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
