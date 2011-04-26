/*
 * Copyright (c) 2011 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.ui.util;

import static java.lang.Character.toLowerCase;

import com.google.inject.Singleton;

/**
 * Utility methods related to {@code String}.
 *
 * @author alruiz@google.com (Alex Ruiz)
 */
@Singleton
public class Strings {

  public String firstCharToLowerCase(String s) {
    if (s == null) return null;
    if (s.length() == 0) return s;
    char[] chars = s.toCharArray();
    chars[0] = toLowerCase(chars[0]);
    return new String(chars);
  }

}
