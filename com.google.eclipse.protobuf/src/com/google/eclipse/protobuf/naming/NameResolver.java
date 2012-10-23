/*
 * Copyright (c) 2011 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.naming;

import static org.eclipse.xtext.util.SimpleAttributeResolver.NAME_RESOLVER;

import static com.google.eclipse.protobuf.grammar.CommonKeyword.DEFAULT;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;

import com.google.eclipse.protobuf.protobuf.DefaultValueFieldOption;
import com.google.inject.Singleton;

/**
 * Resolves names of elements in the protobuf grammar.
 *
 * @author alruiz@google.com (Alex Ruiz)
 */
@Singleton public class NameResolver {
  /**
   * Returns the name of the given element.
   * @param o the given element.
   * @return the name of the given element, or {@code null} if the given element does not have support for naming.
   */
  public String nameOf(EObject o) {
    if (o == null) {
      return null;
    }
    if (o instanceof DefaultValueFieldOption) {
      return DEFAULT.toString();
    }
    Object value = nameFeatureOf(o);
    return (String) value;
  }

  private Object nameFeatureOf(EObject e) {
    EStructuralFeature feature = NAME_RESOLVER.getAttribute(e);
    return (feature != null) ? e.eGet(feature) : null;
  }
}
