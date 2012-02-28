/*
 * Copyright (c) 2012 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.cdt.util;

import static java.util.Collections.*;

import java.util.List;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.*;

/**
 * {@code Iterator} for {@code List}s. This implementation keeps track of the index of the current element to be able
 * to:
 * <ul>
 * <li>Retrieve the elements that have not been visited yet</li>
 * <li>Check whether we are visiting the last element of the {@code List}</li>
 * @param <T> the generic type of the iterator.
 *
 * @author alruiz@google.com (Alex Ruiz)
 */
public class ExtendedListIterator<T> extends AbstractIterator<T> implements ExtendedIterator<T> {
  private static final ExtendedIterator<Object> EMPTY = newIterator(emptyList());

  private final List<T> list;
  private final int listSize;

  private int index = -1;

  /**
   * Creates a new <code>{@link ExtendedListIterator}</code>.
   * @param elements the elements of the {@code Iterator} to create.
   * @return the created {@code ListIterator}.
   */
  public static <T> ExtendedIterator<T> newIterator(T...elements) {
    return new ExtendedListIterator<T>(Lists.newArrayList(elements));
  }

  /**
   * Creates a new <code>{@link ExtendedListIterator}</code>.
   * @param list the {@code List} to iterate.
   * @return the created {@code ListIterator}.
   */
  public static <T> ExtendedIterator<T> newIterator(List<T> list) {
    return new ExtendedListIterator<T>(list);
  }

  @VisibleForTesting ExtendedListIterator(List<T> list) {
    this.list = list;
    listSize = list.size();
  }

  /** {@inheritDoc} */
  @Override @SuppressWarnings("unchecked")
  public ExtendedIterator<T> notRetrievedYet() {
    if (listSize == 0) {
      return (ExtendedIterator<T>) EMPTY;
    }
    List<T> remaining = list.subList(index + 1, listSize);
    return newIterator(remaining);
  }

  /** {@inheritDoc} */
  @Override public boolean wasLastListElementRetrieved() {
    return listSize > 0 && index == listSize - 1;
  }

  /** {@inheritDoc} */
  @Override public ExtendedIterator<T> copy() {
    return new ExtendedListIterator<T>(list);
  }

  @Override protected T computeNext() {
    if (index + 1 < listSize && listSize > 0) {
      return list.get(++index);
    }
    return endOfData();
  }

  @Override public String toString() {
    return list.toString();
  }

  @VisibleForTesting List<T> contents() {
    return unmodifiableList(list);
  }

}
