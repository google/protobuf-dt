/*
 * Copyright (c) 2012 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.cdt.junit;

import static com.google.common.collect.Lists.newArrayList;

import java.util.List;

import org.eclipse.xtext.naming.QualifiedName;
import org.hamcrest.*;

/**
 * @author alruiz@google.com (Alex Ruiz)
 */
public class QualifiedNamesContain extends BaseMatcher<List<QualifiedName>> {
  private final List<String> qualifiedNames;

  public static QualifiedNamesContain containOnly(String...qualifiedNames) {
    return new QualifiedNamesContain(qualifiedNames);
  }

  private QualifiedNamesContain(String[] qualifiedNames) {
    this.qualifiedNames = newArrayList(qualifiedNames);
  }

  @SuppressWarnings("unchecked")
  @Override public boolean matches(Object item) {
    if (!(item instanceof List)) {
      return false;
    }
    List<String> copy = newArrayList(qualifiedNames);
    List<QualifiedName> actualNames = (List<QualifiedName>) item;
    for (QualifiedName actual : actualNames) {
      String expected = actual.toString();
      copy.remove(expected);
    }
    return copy.isEmpty();
  }

  @Override public void describeTo(Description description) {
    description.appendValue(qualifiedNames);
  }
}
