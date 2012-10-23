/*
 * Copyright (c) 2012 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.model.util;

import static com.google.common.collect.Lists.newArrayList;

import java.util.Collection;
import java.util.List;

import org.eclipse.xtext.naming.QualifiedName;
import org.hamcrest.Description;
import org.hamcrest.TypeSafeMatcher;

/**
 * @author alruiz@google.com (Alex Ruiz)
 */
class QualifiedNameCollectionContains extends TypeSafeMatcher<Collection<QualifiedName>> {
  private final List<String> qualifiedNames;

  static QualifiedNameCollectionContains contains(String...qualifiedNames) {
    return new QualifiedNameCollectionContains(qualifiedNames);
  }

  private QualifiedNameCollectionContains(String[] qualifiedNames) {
    this.qualifiedNames = newArrayList(qualifiedNames);
  }

  @Override public boolean matchesSafely(Collection<QualifiedName> item) {
    if (item.size() != qualifiedNames.size()) {
      return false;
    }
    List<String> copy = newArrayList(qualifiedNames);
    for (QualifiedName name : item) {
      copy.remove(name.toString());
    }
    return copy.isEmpty();
  }

  @Override public void describeTo(Description description) {
    description.appendValue(qualifiedNames);
  }
}
