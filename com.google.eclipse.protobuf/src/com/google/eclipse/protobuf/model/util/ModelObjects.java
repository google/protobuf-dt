/*
 * Copyright (c) 2011 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.model.util;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.resource.Resource;

import com.google.eclipse.protobuf.protobuf.Package;
import com.google.eclipse.protobuf.protobuf.Protobuf;
import com.google.eclipse.protobuf.protobuf.ProtobufElement;
import com.google.inject.Singleton;

/**
 * Utility methods related to model objects.
 *
 * @author alruiz@google.com (Alex Ruiz)
 */
@Singleton public class ModelObjects {
  /**
   * Returns the value of the structural feature with a matching name in the given model object.
   * @param o the given model object.
   * @param featureName the name of the structural feature to read the value from.
   * @param valueType the expected type of the value to return.
   * @return the value of the structural feature with a matching name in the given model object, or {@code null} if the
   * given model object is {@code null} or if the model object does not have a structural feature with a matching name.
   * @throws ClassCastException if the value of the structural value is not the same as the expected one.
   */
  public <T> T valueOfFeature(EObject o, String featureName, Class<T> valueType) {
    if (o != null) {
      EStructuralFeature feature = o.eClass().getEStructuralFeature(featureName);
      if (feature != null) {
        return valueType.cast(o.eGet(feature));
      }
    }
    return null;
  }

  /**
   * Returns the package of the root containing the given model object.
   * @param o the given model object.
   * @return the package of the root containing the given model object or {@code null} if the root does not have a
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
   * Returns the root element containing the given model element.
   * @param o the given model element.
   * @return the root element containing the given model element.
   */
  public Protobuf rootOf(EObject o) {
    EObject current = o;
    while (!(current instanceof Protobuf)) {
      current = current.eContainer();
    }
    return (Protobuf) current;
  }

  /**
   * Returns the URI of the given model element.
   * @param e the given model element.
   * @return the URI of the given model element.
   */
  public URI uriOf(EObject e) {
    Resource resource = e.eResource();
    URI uri = resource.getURI();
    return uri.appendFragment(resource.getURIFragment(e));
  }
}
