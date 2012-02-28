/*
 * Copyright (c) 2012 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.cdt.matching;

import static com.google.common.collect.Lists.newArrayList;

import java.util.List;
import java.util.regex.Pattern;

import com.google.eclipse.protobuf.cdt.mapping.CppToProtobufMapping;
import com.google.inject.Singleton;

/**
 * @author alruiz@google.com (Alex Ruiz)
 */
@Singleton class PatternBuilder {
  Pattern patternToMatchFrom(CppToProtobufMapping mapping) {
    StringBuilder regex = new StringBuilder();
    List<String> segments = newArrayList(mapping.qualifiedName());
    int segmentCount = segments.size();
    for (int i = 0; i < segmentCount; i++) {
      String segment = segments.get(i);
      regex.append(segment.replaceAll("_", "(\\\\.|_)"));
      if (i < segmentCount - 1) {
        regex.append("\\.");
      }
    }
    return Pattern.compile(regex.toString());
  }
}
