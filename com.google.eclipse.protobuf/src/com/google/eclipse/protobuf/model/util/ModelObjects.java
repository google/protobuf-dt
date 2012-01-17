/*
 * Copyright (c) 2011 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.model.util;

import com.google.eclipse.protobuf.protobuf.*;
import com.google.eclipse.protobuf.protobuf.Package;
import com.google.inject.Singleton;

import org.eclipse.emf.ecore.*;

/**
 * Utility methods related to model objects.
 *
 * @author alruiz@google.com (Alex Ruiz)
 */
@Singleton public class ModelObjects {
  /**
   * Returns the value of the structural feature with a matching name in the given model object.
   * @param e the given model object.
   * @param featureName the name of the structural feature to read the value from.
   * @param valueType the expected type of the value to return.
   * @return the value of the structural feature with a matching name in the given model object, or {@code null} if the
   * given model object is {@code null} or if the model object does not have a structural feature with a matching name.
   * @throws ClassCastException if the value of the structural value is not the same as the expected one.
   */
  public <T> T valueOfFeature(EObject e, String featureName, Class<T> valueType) {
    if (e != null) {
      EStructuralFeature feature = e.eClass().getEStructuralFeature(featureName);
      if (feature != null) {
        return valueType.cast(e.eGet(feature));
      }
    }
    return null;
  }

  /**
   * Returns the package of the proto file containing the given object.
   * @param o the given object.
   * @return the package of the proto file containing the given object or {@code null} if the proto file does not have a
   * package.
   */
  public Package packageOf(EObject o) {
    Protobuf root = rootOf(o);
    for (ProtobufElement e : root.getElements()) {
      if (e instanceof Package) {
        return (Package) e;
      }
    }
    return null;
  }

  /**
   * Returns the root element of the proto file containing the given element.
   * @param o the given element.
   * @return the root element of the proto file containing the given element.
   */
  public Protobuf rootOf(EObject o) {
    EObject current = o;
    while (!(current instanceof Protobuf)) {
      current = current.eContainer();
    }
    return (Protobuf) current;
  }
}
