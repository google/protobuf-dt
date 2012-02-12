/*
 * Copyright (c) 2012 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.cdt.matching;

import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Maps.newHashMap;
import static java.util.Collections.*;

import java.util.*;

import org.eclipse.emf.ecore.*;

/**
 * @author alruiz@google.com (Alex Ruiz)
 */
class ContentsByType {
  private final Map<EClass, List<EObject>> contentsByType = newHashMap();

  static ContentsByType contentsOf(EObject target) {
    return new ContentsByType(target);
  }

  private ContentsByType(EObject target) {
    for (EObject child : target.eContents()) {
      EClass type = child.eClass();
      List<EObject> children = contentsByType.get(type);
      if (children == null) {
        children = newArrayList();
        contentsByType.put(type, children);
      }
      children.add(child);
    }
  }

  List<EObject> ofType(EClass type) {
    List<EObject> children = contentsByType.get(type);
    if (children == null) {
      return emptyList();
    }
    return unmodifiableList(children);
  }
}
