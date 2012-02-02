/*
 * Copyright (c) 2012 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.cdt.actions;

import static com.google.common.collect.Lists.newArrayList;
import static java.util.Collections.unmodifiableList;

import java.util.List;

import org.eclipse.xtext.naming.QualifiedName;

import com.google.eclipse.protobuf.model.util.QualifiedNames;
import com.google.inject.*;

/**
 * @author alruiz@google.com (Alex Ruiz)
 */
@Singleton class QualifiedNameFactory {
  @Inject private QualifiedNames qualifiedNames;

  List<QualifiedName> createQualifiedNamesForComplexType(String[] segments) {
    List<QualifiedName> names = newArrayList();
    names.add(QualifiedName.create(segments));
    int lastSegmentIndex = segments.length - 1;
    String messageName = segments[lastSegmentIndex];
    if (messageName.contains("_")) {
      String[] hierarchicalNames = messageName.split("_");
      List<String> newSegments = newArrayList(segments);
      newSegments.remove(lastSegmentIndex);
      newSegments.addAll(newArrayList(hierarchicalNames));
      names.add(qualifiedNames.createFqn(newSegments));
    }
    return unmodifiableList(names);
  }
}
