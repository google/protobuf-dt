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
 * System properties.
 *
 * @author alruiz@google.com (Alex Ruiz)
 */
public final class SystemProperties {
  private static final String LINE_SEPARATOR = System.getProperty("line.separator");

  public static String lineSeparator() {
    return LINE_SEPARATOR;
  }

  private SystemProperties() {}
}
