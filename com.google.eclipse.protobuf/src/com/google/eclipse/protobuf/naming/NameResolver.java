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

import com.google.inject.Singleton;

import org.eclipse.emf.ecore.*;

/**
 * @author alruiz@google.com (Alex Ruiz)
 */
@Singleton
public class NameResolver {

  public String nameOf(EObject o) {
    Object value = nameFeatureOf(o);
    if (value instanceof String) return (String) value;
    return null;
  }
  
  private Object nameFeatureOf(EObject e) {
    EStructuralFeature f = NAME_RESOLVER.getAttribute(e);
    return (f != null) ? e.eGet(f) : null;
  }
}
