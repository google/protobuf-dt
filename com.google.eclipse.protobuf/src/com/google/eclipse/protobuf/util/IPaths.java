/*
 * Copyright (c) 2012 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.util;

import static com.google.common.base.Objects.equal;

import org.eclipse.core.runtime.IPath;
import org.eclipse.emf.common.util.URI;

import com.google.inject.Singleton;

/**
 * Utility methods related to <code>{@link IPath}</code>s.
 *
 * @author alruiz@google.com (Alex Ruiz)
 */
@Singleton public class IPaths {

  /**
   * Indicates whether the given path and URI refer to the same file.
   * @param p the given path.
   * @param u the given URI.
   * @return {@code true} if the given path and URI refer to the same file, {@code false} otherwise.
   */
  public boolean areReferringToSameFile(IPath p, URI u) {
    // TODO test
    int pIndex = p.segmentCount() - 1;
    int uIndex = u.segmentCount() - 1;
    while (pIndex >= 0 && uIndex >= 0) {
      String pSegment = p.segment(pIndex--);
      String uSegment = u.segment(uIndex--);
      if (!equal(pSegment, uSegment)) {
        return false;
      }
    }
    return true;
  }
}
