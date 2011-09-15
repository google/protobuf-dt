/*
 * Copyright (c) 2011 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.ui.util;

import static java.io.File.separator;

/**
 * Utility methods related to paths.
 *
 * @author alruiz@google.com (Alex Ruiz)
 */
public final class Paths {

  /**
   * Returns the segments of the given path.
   * @param path the given path.
   * @return the segments of the given path.
   * @throws NullPointerException if the given path is {@code null}.
   */
  public static String[] segmentsOf(String path) {
    if (path == null) throw new NullPointerException("The given path should not be null");
    return path.split("\\Q" + separator + "\\E");
  }

  private Paths() {}
}
