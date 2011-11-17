/*
 * Copyright (c) 2011 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.naming;

import com.google.eclipse.protobuf.protobuf.Name;
import com.google.eclipse.protobuf.protobuf.Package;
import com.google.inject.Singleton;

import org.eclipse.emf.ecore.*;

import java.util.List;

/**
 * @author alruiz@google.com (Alex Ruiz)
 */
@Singleton
public class NameResolver {

  public String nameOf(EObject o) {
    if (o instanceof Package) return nameOf((Package) o);
    Object value = nameFeatureOf(o);
    if (value instanceof Name) return ((Name) value).getValue();
    return null;
  }

  private String nameOf(Package p) {
    List<Name> segments = p.getSegments();
    int segmentCount = segments.size();
    if (segmentCount == 0) return null;
    StringBuilder b = new StringBuilder();
    for (int i = 0; i < segmentCount; i++) {
      b.append(segments.get(i).getValue());
      if (i < segmentCount - 1) b.append(".");
    }
    return b.toString();
  }
  
  
  private Object nameFeatureOf(EObject e) {
    EStructuralFeature f = e.eClass().getEStructuralFeature("name");
    return (f != null) ? e.eGet(f) : null;
  }
}
