/*
 * Copyright (c) 2011 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.scoping;

import static java.util.Collections.*;

import com.google.eclipse.protobuf.protobuf.Package;
import com.google.inject.Inject;

import org.eclipse.xtext.naming.*;

import java.util.*;

/**
 * @author alruiz@google.com (Alex Ruiz)
 */
class PackageIntersection {

  @Inject private final IQualifiedNameConverter converter = new IQualifiedNameConverter.DefaultImpl();

  List<String> intersection(Package p1, Package p2) {
    if (p1 == null || p2 == null) return emptyList();
    return intersection(converter.toQualifiedName(p1.getName()), converter.toQualifiedName(p2.getName()));
  }

  private List<String> intersection(QualifiedName n1, QualifiedName n2) {
    return intersection(n1.getSegments(), n2.getSegments());
  }

  private List<String> intersection(List<String> n1, List<String> n2) {
    List<String> intersection = new ArrayList<String>();
    int n1Count = n1.size();
    int n2Count = n2.size();
    int start = -1;
    for (int i = 0; (i < n1Count && i < n2Count); i++) {
      if (!n1.get(i).equals(n2.get(i))) {
        start = i;
        break;
      }
    }
    if (start >= 0) {
      for (int i = start; i < n2Count; i++) {
        intersection.add(n2.get(i));
      }
    }
    if (intersection.equals(n2)) return emptyList();
    return unmodifiableList(intersection);
  }

}
