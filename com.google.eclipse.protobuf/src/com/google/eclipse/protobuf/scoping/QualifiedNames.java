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

import java.util.*;

import org.eclipse.xtext.naming.*;

import com.google.eclipse.protobuf.protobuf.Package;
import com.google.inject.Inject;

/**
 * @author alruiz@google.com (Alex Ruiz)
 */
class QualifiedNames {

  @Inject private final IQualifiedNameConverter converter = new IQualifiedNameConverter.DefaultImpl();

  QualifiedName addLeadingDot(QualifiedName name) {
    if (name.getFirstSegment().equals("")) return name;
    List<String> segments = new ArrayList<String>();
    segments.addAll(name.getSegments());
    segments.add(0, "");
    return QualifiedName.create(segments.toArray(new String[segments.size()]));
  }

  Collection<QualifiedName> addPackageNameSegments(QualifiedName name, Package p) {
    QualifiedName current = name;
    List<String> segments = fqnSegments(p);
    int segmentCount = segments.size();
    if (segmentCount <= 1) return emptyList();
    List<QualifiedName> allNames = new ArrayList<QualifiedName>();
    for (int i = segmentCount - 1; i > 0; i--) {
      current = QualifiedName.create(segments.get(i)).append(current);
      allNames.add(current);
    }
    return unmodifiableList(allNames);
  }

  private List<String> fqnSegments(Package p) {
    if (p == null) return emptyList();
    String packageName = p.getName();
    if (isEmpty(packageName)) return emptyList();
    return converter.toQualifiedName(packageName).getSegments();
  }
}
