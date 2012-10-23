/*
 * Copyright (c) 2011 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.ui.contentassist;

import static com.google.common.collect.Lists.newArrayList;

import java.util.Collection;
import java.util.List;

import org.eclipse.xtext.naming.QualifiedName;
import org.eclipse.xtext.resource.IEObjectDescription;
import org.hamcrest.Description;
import org.hamcrest.TypeSafeMatcher;

/**
 * @author alruiz@google.com (Alex Ruiz)
 */
public class IEObjectDescriptionsHaveNames extends TypeSafeMatcher<Collection<IEObjectDescription>> {
  private final List<String> qualifiedNames;

  public static IEObjectDescriptionsHaveNames containOnly(String...names) {
    return new IEObjectDescriptionsHaveNames(names);
  }

  private IEObjectDescriptionsHaveNames(String[] qualifiedNames) {
    this.qualifiedNames = newArrayList(qualifiedNames);
  }

  @Override public boolean matchesSafely(Collection<IEObjectDescription> item) {
    List<String> copyOfNames = newArrayList(qualifiedNames);
    if (copyOfNames.size() != item.size()) {
      return false;
    }
    for (IEObjectDescription description : item) {
      QualifiedName qualifiedName = description.getName();
      if (qualifiedName == null) {
        continue;
      }
      if (!copyOfNames.remove(qualifiedName.toString())) {
        return false;
      }
    }
    return copyOfNames.isEmpty();
  }

  /** {@inheritDoc} */
  @Override public void describeTo(Description description) {
    description.appendValue(qualifiedNames);
  }
}
