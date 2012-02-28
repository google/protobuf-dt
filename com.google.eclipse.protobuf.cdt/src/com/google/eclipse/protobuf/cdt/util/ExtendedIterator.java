/*
 * Copyright (c) 2012 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.cdt.util;

import java.util.Iterator;

/**
 * {@code Iterator} that keeps track of the last retrieved element.
 * @param <T> the generic type of this {@code Iterator}.
 *
 * @author alruiz@google.com (Alex Ruiz)
 */
public interface ExtendedIterator<T> extends Iterator<T> {

  /**
   * Returns the elements that have not been retrieved yet.
   * @return the elements that have not been retrieved yet.
   */
  ExtendedIterator<T> notRetrievedYet();

  /**
   * Indicates whether the last retrieved element is the last element in the {@code List}.
   * @return {@code true} if the last retrieved element is the last element in the {@code List}; {@code false} otherwise.
   */
  boolean wasLastListElementRetrieved();

  /**
   * Returns a copy of this {@code Iterator}.
   * @return a copy of this {@code Iterator}.
   */
  ExtendedIterator<T> copy();
}