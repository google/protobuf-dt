/*
 * Copyright (c) 2014 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.model.util;

import static com.google.eclipse.protobuf.util.Strings.unquote;
import static org.eclipse.xtext.util.Strings.convertToJavaString;

import com.google.eclipse.protobuf.protobuf.StringLiteral;
import com.google.inject.Singleton;

/**
 * Helper methods for working with {@link StringLiteral}s.
 */
@Singleton public class StringLiterals {
  /**
   * Returns the result of combining the chunks of a string literal.
   */
  public String getCombinedString(StringLiteral stringLiteral) {
    StringBuilder sb = new StringBuilder();
    for (String chunk : stringLiteral.getChunks()) {
      sb.append(convertToJavaString(unquote(chunk)));
    }
    return sb.toString();
  }
}
