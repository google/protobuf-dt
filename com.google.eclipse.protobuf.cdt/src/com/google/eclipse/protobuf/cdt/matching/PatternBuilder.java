/*
 * Copyright (c) 2012 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.cdt.matching;

import java.util.regex.Pattern;

import org.eclipse.xtext.naming.IQualifiedNameConverter;

import com.google.eclipse.protobuf.cdt.mapping.CppToProtobufMapping;
import com.google.inject.Inject;

/**
 * @author alruiz@google.com (Alex Ruiz)
 */
class PatternBuilder {
  @Inject private IQualifiedNameConverter converter;

  Pattern patternToMatchFrom(CppToProtobufMapping mapping) {
    String qualifiedNameAsText = converter.toString(mapping.qualifiedName());
    StringBuilder regex = new StringBuilder();
    int size = qualifiedNameAsText.length();
    // escape existing "."
    // replace "_" with "(\.|_)"
    for (int i = 0; i < size; i++) {
      char c = qualifiedNameAsText.charAt(i);
      switch (c) {
        case '.':
          regex.append("\\.");
          break;
        case '_':
          regex.append("(\\.|_)");
          break;
        default:
          regex.append(c);
      }
    }
    return Pattern.compile(regex.toString());
  }
}
