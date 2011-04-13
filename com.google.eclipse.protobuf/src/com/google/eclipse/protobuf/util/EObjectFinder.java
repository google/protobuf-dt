/*
 * Copyright (c) 2011 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.util;

import org.eclipse.emf.ecore.EObject;

import com.google.eclipse.protobuf.protobuf.*;
import com.google.eclipse.protobuf.protobuf.Enum;
import com.google.eclipse.protobuf.protobuf.Package;
import com.google.inject.Singleton;

/**
 * @author alruiz@google.com (Alex Ruiz)
 */
@Singleton
public class EObjectFinder {

  public Enum enumTypeOfProperty(Property p) {
    AbstractTypeReference aTypeRef = (p).getType();
    if (aTypeRef instanceof TypeReference) {
      Type type = ((TypeReference) aTypeRef).getType();
      if (type instanceof Enum) return (Enum) type;
    }
    return null;
  }

  public ScalarType scalarTypeOfProperty(Property p) {
    AbstractTypeReference aTypeRef = (p).getType();
    if (aTypeRef instanceof ScalarTypeReference)
      return ((ScalarTypeReference) aTypeRef).getScalar();
    return null;
  }

  public Package findPackage(EObject o) {
    EObject current = o;
    while (current != null) {
      if (current instanceof Protobuf) return ((Protobuf) current).getPackage();
      current = current.eContainer();
    }
    return null;
  }
}
