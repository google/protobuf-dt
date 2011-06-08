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
import static org.eclipse.xtext.util.Strings.isEmpty;

import com.google.eclipse.protobuf.protobuf.Package;

import org.eclipse.xtext.naming.*;

import java.util.*;

/**
 * @author alruiz@google.com (Alex Ruiz)
 */
final class QualifiedNames {

  static List<QualifiedName> addPackageNameSegments(QualifiedName qualifiedName, Package p,
      IQualifiedNameConverter converter) {
    QualifiedName name = qualifiedName;
    List<String> segments = fqnSegments(p, converter);
    int segmentCount = segments.size();
    if (segmentCount <= 1) return emptyList();
    List<QualifiedName> allNames = new ArrayList<QualifiedName>();
    for (int i = segmentCount - 1; i > 0; i--) {
      name = QualifiedName.create(segments.get(i)).append(name);
      allNames.add(name);
    }
    return unmodifiableList(allNames);
  }

  static private List<String> fqnSegments(Package p, IQualifiedNameConverter converter) {
    if (p == null) return emptyList();
    String packageName = p.getName();
    if (isEmpty(packageName)) return emptyList();
    return converter.toQualifiedName(packageName).getSegments();
  }
  
  private QualifiedNames() {}
}
