/*
 * Copyright (c) 2011 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.util;

/**
 * Utility methods related to {@code String}.s
 *
 * @author alruiz@google.com (Alex Ruiz)
 */
public final class Strings {

  /**
   * Returns the given {@code String} in double quotes.
   * @param s the given {@code String}, may be {@code null}.
   * @return the given {@code String} in double quotes, or {@code null} if the given {@code String} is {@code null}.
   */
  public static String quote(String s) {
    if (s == null) {
      return s;
    }
    return "\"" + s + "\"";
  }

  private Strings() {}
}
