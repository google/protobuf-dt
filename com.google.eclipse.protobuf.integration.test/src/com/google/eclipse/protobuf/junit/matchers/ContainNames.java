/*
 * Copyright (c) 2011 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.junit.matchers;

import static java.util.Arrays.asList;

import java.util.Collection;
import java.util.List;

import org.hamcrest.Description;
import org.hamcrest.TypeSafeMatcher;

import com.google.eclipse.protobuf.junit.IEObjectDescriptions;

/**
 * @author alruiz@google.com (Alex Ruiz)
 */
public class ContainNames extends TypeSafeMatcher<IEObjectDescriptions> {
  private final List<String> expectedNames;

  public static ContainNames contain(String... names) {
    return new ContainNames(names);
  }

  private ContainNames(String... names) {
    super(IEObjectDescriptions.class);
    expectedNames = asList(names);
  }

  @Override public boolean matchesSafely(IEObjectDescriptions item) {
    Collection<String> names = item.names();
    return names.containsAll(expectedNames);
  }

  @Override public void describeTo(Description description) {
    description.appendValue(expectedNames);
  }
}
