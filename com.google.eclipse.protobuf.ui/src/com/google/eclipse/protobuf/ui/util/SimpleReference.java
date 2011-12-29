/*
 * Copyright (c) 2011 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License v1.0 which
 * accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.ui.util;

/**
 * A reference to an object. This class is not thread-safe.
 * @param <T> the type of object this reference holds.
 *
 * @author alruiz@google.com (Alex Ruiz)
 */
public class SimpleReference<T> {
  private T value;

  /**
   * Creates a new <code>{@link SimpleReference}</code> with a {@code null} value.
   */
  public SimpleReference() {}

  /**
   * Creates a new <code>{@link SimpleReference}</code>.
   * @param value the initial value of this reference.
   */
  public SimpleReference(T value) {
    this.value = value;
  }

  /**
   * Returns this reference's value.
   * @return this reference's value.
   */
  public T get() {
    return value;
  }

  /**
   * Sets this reference's value.
   * @param newValue the new value to set.
   */
  public void set(T newValue) {
    value = newValue;
  }
}
