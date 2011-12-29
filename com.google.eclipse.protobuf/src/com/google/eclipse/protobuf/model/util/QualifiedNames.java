/*
 * Copyright (c) 2011 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.model.util;

import java.util.*;

import org.eclipse.xtext.naming.QualifiedName;

import com.google.inject.Singleton;

/**
 * Utility methods related to <code>{@link QualifiedName}</code>s.
 *
 * @author alruiz@google.com (Alex Ruiz)
 */
@Singleton public class QualifiedNames {
  public QualifiedName createFqn(List<String> segments) {
    return QualifiedName.create(asArray(segments));
  }

  private String[] asArray(List<String> list) {
    return list.toArray(new String[list.size()]);
  }

  public QualifiedName addLeadingDot(QualifiedName name) {
    if (name.getFirstSegment().equals("")) {
      return name;
    }
    List<String> segments = new ArrayList<String>();
    segments.addAll(name.getSegments());
    segments.add(0, "");
    return QualifiedName.create(segments.toArray(new String[segments.size()]));
  }
}
