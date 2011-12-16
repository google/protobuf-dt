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
 * Utility methods related to objects.
 *
 * @author alruiz@google.com (Alex Ruiz)
 */
public final class Objects {

  /** Prime number used to calculate the hash code of objects. */
  public static final int HASH_CODE_PRIME = 31;

  /**
   * Returns {@code true} if the given objects are equal or if both objects are {@code null}.
   * @param o1 one of the objects to compare.
   * @param o2 one of the objects to compare.
   * @return {@code true} if the given objects are equal or if both objects are {@code null}.
   */
  public static boolean areEqual(Object o1, Object o2) {
    if (o1 == null) {
      return o2 == null;
    }
    return o1.equals(o2);
  }

  /**
   * Returns the hash code for the given object. If the object is {@code null}, this method returns zero. Otherwise
   * calls the method {@code hashCode} of the given object.
   * @param o the given object.
   * @return the hash code for the given object
   */
  public static int hashCodeOf(Object o) {
    return o != null ? o.hashCode() : 0;
  }

  private Objects() {}
}
