/*
 * Copyright (c) 2012 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.util;

import java.util.List;

import com.google.inject.Singleton;

/**
 * Utility methods related to <code>{@link List}</code>s of {@code String}s.
 *
 * @author alruiz@google.com (Alex Ruiz)
 */
@Singleton public class StringLists {
  /**
   * Returns an array containing all of the elements in the given list in proper sequence (from first to last element.)
   * @param list the given list.
   * @return an array containing all of the elements in the given list in proper sequence.
   */
  public String[] toArray(List<String> list) {
    return list.toArray(new String[list.size()]);
  }
}
