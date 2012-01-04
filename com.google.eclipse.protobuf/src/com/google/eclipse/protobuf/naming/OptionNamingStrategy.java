/*
 * Copyright (c) 2012 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.naming;

import org.eclipse.emf.ecore.EObject;

import com.google.eclipse.protobuf.model.util.Options;
import com.google.eclipse.protobuf.protobuf.IndexedElement;

/**
 * @author alruiz@google.com (Alex Ruiz)
 */
class OptionNamingStrategy implements NamingStrategy {
  private final NameResolver nameResolver;
  private final Options options;

  OptionNamingStrategy(NameResolver nameResolver, Options options) {
    this.nameResolver = nameResolver;
    this.options = options;
  }


  @Override public String nameOf(EObject e) {
    if (e instanceof IndexedElement) {
      return options.nameForOption((IndexedElement) e);
    }
    return nameResolver.nameOf(e);
  }
}
