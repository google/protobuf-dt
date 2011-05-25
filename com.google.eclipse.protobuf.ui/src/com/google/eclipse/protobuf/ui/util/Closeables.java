/*
 * Copyright (c) 2011 Google Inc. All rights reserved. This program and the accompanying materials are made available
 * under the terms of the Eclipse Public License v1.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.ui.util;

import java.io.Closeable;
import java.io.IOException;

import com.google.inject.Singleton;

/**
 * Utility methods related to <code>{@link Closeable}</code>.
 * 
 * @author alruiz@google.com (Alex Ruiz)
 */
@Singleton
public class Closeables {

  public boolean close(Closeable c) {
    if (c == null) return false;
    try {
      c.close();
    } catch (IOException ignored) {}
    return true;
  }
}
