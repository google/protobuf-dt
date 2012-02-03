/*
 * Copyright (c) 2012 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.cdt.fqn;

import static com.google.common.collect.Lists.newArrayList;

import java.util.*;

import org.eclipse.xtext.naming.QualifiedName;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.AbstractIterator;

/**
 * @author alruiz@google.com (Alex Ruiz)
 */
class QualifiedNameSource implements Iterable<QualifiedName> {
  private final QualifiedName original;

  public QualifiedNameSource(String[] segments) {
    original = QualifiedName.create(segments);
  }

  @VisibleForTesting QualifiedName original() {
    return original;
  }

  @Override public Iterator<QualifiedName> iterator() {
    return new QualifiedNameIterator();
  }

  private class QualifiedNameIterator extends AbstractIterator<QualifiedName> {
    private int sentCount;

    @Override protected QualifiedName computeNext() {
      switch (sentCount++) {
        case 0:
          return original;
        case 1:
          return nested();
        default:
          return endOfData();
      }
    }

    private QualifiedName nested() {
      String name = original.getLastSegment();
      if (!name.contains("_")) {
        return endOfData();
      }
      String[] nestedNames = name.split("_");
      List<String> newSegments = newArrayList(original.getSegments());
      newSegments.remove(newSegments.size() - 1);
      newSegments.addAll(newArrayList(nestedNames));
      String[] segments = newSegments.toArray(new String[newSegments.size()]);
      return QualifiedName.create(segments);
    }
  }
}
