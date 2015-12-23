/*
 * Copyright (c) 2012 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.cdt.util;

import static org.hamcrest.collection.IsCollectionContaining.hasItems;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import static com.google.common.collect.Lists.newArrayList;

import java.util.List;

import org.junit.Before;
import org.junit.Test;

/**
 * Tests for <code>{@link ExtendedListIterator}</code>.
 *
 * @author alruiz@google.com (Alex Ruiz)
 */
public class ExtendedListIterator_Test {
  private List<String> list;
  private ExtendedListIterator<String> iterator;

  @Before public void setUp() {
    list = newArrayList("Luke", "Yoda", "Leia");
    iterator = new ExtendedListIterator<String>(list);
  }

  @Test public void should_iterate_through_list() {
    assertTrue(iterator.hasNext());
    assertThat(iterator.next(), equalTo("Luke"));
    assertTrue(iterator.hasNext());
    assertThat(iterator.next(), equalTo("Yoda"));
    assertTrue(iterator.hasNext());
    assertThat(iterator.next(), equalTo("Leia"));
    assertFalse(iterator.hasNext());
  }

  @Test public void should_return_elements_that_have_not_been_visited_yet() {
    assertThat(contentsOf(iterator.notRetrievedYet()), hasItems("Luke", "Yoda", "Leia"));
    iterator.next();
    assertThat(contentsOf(iterator.notRetrievedYet()), hasItems("Yoda", "Leia"));
    iterator.next();
    assertThat(contentsOf(iterator.notRetrievedYet()), hasItems("Leia"));
    iterator.next();
    assertTrue(contentsOf(iterator.notRetrievedYet()).isEmpty());
  }

  private <T> List<T> contentsOf(ExtendedIterator<T> iterator) {
    return ((ExtendedListIterator<T>) iterator).contents();
  }

  @Test public void should_indicate_if_last_list_element_was_retrieved() {
    assertFalse(iterator.wasLastListElementRetrieved());
    iterator.next(); // Luke
    assertFalse(iterator.wasLastListElementRetrieved());
    iterator.next(); // Yoda
    assertFalse(iterator.wasLastListElementRetrieved());
    iterator.next(); // Leia
    assertTrue(iterator.wasLastListElementRetrieved());
  }
}
