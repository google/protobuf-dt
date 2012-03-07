/*
 * Copyright (c) 2011 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.junit.util;

import static com.google.eclipse.protobuf.util.SystemProperties.lineSeparator;

/**
 * @author alruiz@google.com (Alex Ruiz)
 */
public class MultiLineTextBuilder {
  private final StringBuilder builder = new StringBuilder();

  private int currentLineCount;

  public MultiLineTextBuilder() {}

  public MultiLineTextBuilder(String initialContent) {
    append(initialContent);
  }

  public MultiLineTextBuilder append(String s) {
    if (currentLineCount++ > 0) {
      builder.append(lineSeparator());
    }
    builder.append(s);
    return this;
  }

  public boolean isEmpty() {
    return builder.length() == 0;
  }

  public boolean hasContent(String s) {
    return toString().equals(s);
  }

  @Override public String toString() {
    return builder.toString();
  }
}
