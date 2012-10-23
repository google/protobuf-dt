/*
 * Copyright (c) 2012 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.naming;

import static org.eclipse.xtext.util.Strings.isEmpty;
import static org.eclipse.xtext.util.Tuples.pair;

import static com.google.eclipse.protobuf.naming.NameType.NORMAL;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.xtext.util.Pair;

import com.google.inject.Inject;
import com.google.inject.Singleton;

/**
 * Returns the name of a model object obtained from a <code>{@link NameResolver}</code>.
 *
 * @author alruiz@google.com (Alex Ruiz)
 */
@Singleton public class NormalNamingStrategy implements NamingStrategy {
  @Inject private NameResolver nameResolver;

  /** {@inheritDoc} */
  @Override public Pair<NameType, String> nameOf(EObject e) {
    String value = nameResolver.nameOf(e);
    if (isEmpty(value)) {
      return null;
    }
    return pair(NORMAL, value);
  }
}
