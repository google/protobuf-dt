/*
 * Copyright (c) 2011 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.model.util;

import static com.google.common.collect.Lists.newArrayList;

import java.util.List;

import org.eclipse.xtext.naming.QualifiedName;

import com.google.eclipse.protobuf.util.StringLists;
import com.google.inject.Inject;
import com.google.inject.Singleton;

/**
 * Utility methods related to <code>{@link QualifiedName}</code>s.
 *
 * @author alruiz@google.com (Alex Ruiz)
 */
@Singleton public class QualifiedNames {
  @Inject private StringLists stringLists;

  public QualifiedName addLeadingDot(QualifiedName name) {
    if (name.getFirstSegment().equals("")) {
      return name;
    }
    List<String> segments = newArrayList();
    segments.addAll(name.getSegments());
    segments.add(0, "");
    return createQualifiedName(segments);
  }

  public QualifiedName createQualifiedName(List<String> segments) {
    return QualifiedName.create(stringLists.toArray(segments));
  }
}
