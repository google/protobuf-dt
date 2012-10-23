/*
 * Copyright (c) 2011 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.util;

import static org.eclipse.xtext.util.Strings.isEmpty;

import java.util.Scanner;

import com.google.common.base.Function;

/**
 * Utility methods related to {@code String}.s
 *
 * @author alruiz@google.com (Alex Ruiz)
 */
public final class Strings {
  /**
   * Returns a {@code String} containing the given one in double quotes.
   * @param s the given {@code String}, may be {@code null}.
   * @return a {@code String} containing the given one in double quotes, or {@code null} if the given {@code String} is
   * {@code null}.
   */
  public static String quote(String s) {
    if (s == null) {
      return s;
    }
    return "\"" + s + "\"";
  }

  /**
   * Removes surrounding quotes from the given {@code String}.
   * @param s the given {@code String}, may be {@code null}.
   * @return a {@code String} containing the given one without surrounding quotes, or {@code null} if the given
   * {@code String} is {@code null}.
   */
  public static String unquote(String s) {
    if (!isQuoted(s)) {
      return s;
    }
    return s.substring(1, s.length() - 1);
  }

  private static boolean isQuoted(String s) {
    if (isEmpty(s)) {
      return false;
    }
    return (s.startsWith("\"") && s.endsWith("\"")) || (s.startsWith("'") && s.endsWith("'"));
  }

  /**
   * Returns a {@code String} containing the given one without line breaks.
   * @param s the given {@code String}, may be {@code null}.
   * @return a {@code String} containing the given one without line breaks, or {@code null} if the given {@code String}
   * is {@code null}.
   */
  public static String removeLineBreaksFrom(String s) {
    return removeLineBreaks(s, null);
  }

  /**
   * Returns a {@code String} containing the given one without line breaks.
   * @param s the given {@code String}, may be {@code null}.
   * @param transformation any modifications to apply to each line in the given {@code String}, may be {@code null}.
   * @return a {@code String} containing the given one without line breaks, or {@code null} if the given {@code String}
   * is {@code null}.
   */
  public static String removeLineBreaks(String s, Function<String, String> transformation) {
    if (isEmpty(s)) {
      return s;
    }
    StringBuilder valueBuilder = new StringBuilder();
    Scanner scanner = new Scanner(s);
    try {
      while (scanner.hasNextLine()) {
        String line = scanner.nextLine().trim();
        if (transformation != null) {
          line = transformation.apply(line);
        }
        valueBuilder.append(line);
      }
    } finally {
      try {
        scanner.close();
      } catch (RuntimeException ignored) {}
    }
    return valueBuilder.toString();
  }

  private Strings() {}
}
