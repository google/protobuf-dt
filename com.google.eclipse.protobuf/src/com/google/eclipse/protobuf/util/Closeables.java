/*
 * Copyright (c) 2011 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.util;

import java.io.Closeable;

/**
 * Utility methods related to <code>{@link Closeable}</code>s.
 *
 * @author alruiz@google.com (Alex Ruiz)
 */
public class Closeables {

  /**
   * Invokes {@code close()} on the given <code>{@link Closeable}</code>, ignoring any thrown exceptions.
   * @param c the given {@code Closeable}.
   * @return {@code false} if the given {@code Closeable} was {@code null}; {@code true} otherwise.
   */
  public static boolean closeQuietly(Closeable c) {
    if (c == null) return false;
    try {
      c.close();
    } catch (Throwable ignored) {}
    return true;
  }

  private Closeables() {}
}
