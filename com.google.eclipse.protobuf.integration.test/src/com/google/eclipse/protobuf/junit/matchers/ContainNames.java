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

import java.util.List;

import org.hamcrest.*;

import com.google.eclipse.protobuf.junit.IEObjectDescriptions;

/**
 * @author alruiz@google.com (Alex Ruiz)
 */
public class ContainNames extends BaseMatcher<IEObjectDescriptions> {
  private final List<String> expectedNames;

  public static ContainNames contain(String... names) {
    return new ContainNames(names);
  }

  private ContainNames(String... names) {
    expectedNames = asList(names);
  }

  @Override public boolean matches(Object arg) {
    if (!(arg instanceof IEObjectDescriptions)) {
      return false;
    }
    IEObjectDescriptions descriptions = (IEObjectDescriptions) arg;
    return descriptions.names().containsAll(expectedNames);
  }

  @Override public void describeTo(Description description) {
    description.appendValue(expectedNames);
  }
}
