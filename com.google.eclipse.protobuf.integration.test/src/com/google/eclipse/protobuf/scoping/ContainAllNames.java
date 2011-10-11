/*
 * Copyright (c) 2011 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.scoping;

import org.hamcrest.*;

import java.util.*;

/**
 * @author alruiz@google.com (Alex Ruiz)
 */
public class ContainAllNames extends BaseMatcher<IEObjectDescriptions> {

  private final String[] expectedNames;

  public static ContainAllNames containAll(String... names) {
    return new ContainAllNames(names);
  }
  
  private ContainAllNames(String... names) {
    expectedNames = names;
  }
  
  public boolean matches(Object arg) {
    if (!(arg instanceof IEObjectDescriptions)) return false;
    IEObjectDescriptions descriptions = (IEObjectDescriptions) arg;
    List<String> names = new ArrayList<String>(descriptions.names());
    if (names.size() != expectedNames.length) return false;
    for (String name : expectedNames) {
      boolean removed = names.remove(name);
      if (!removed) return false;
    }
    return names.isEmpty();
  }

  public void describeTo(Description description) {
    description.appendValue(Arrays.toString(expectedNames));
  }
}
