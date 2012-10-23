/*
 * Copyright (c) 2012 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License v1.0 which
 * accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.naming;

import static org.eclipse.xtext.util.Strings.isEmpty;
import static org.eclipse.xtext.util.Tuples.create;

import static com.google.eclipse.protobuf.naming.NameType.OPTION;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.xtext.util.Pair;

import com.google.eclipse.protobuf.model.util.Options;
import com.google.eclipse.protobuf.protobuf.IndexedElement;
import com.google.inject.Inject;
import com.google.inject.Singleton;

/**
 * Returns the name of a model object, to be used as the name of an option.
 *
 * @author alruiz@google.com (Alex Ruiz)
 *
 * @see NameResolver#nameOf(EObject)
 * @see Options#nameForOption(IndexedElement)
 */
@Singleton public class OptionNamingStrategy implements NamingStrategy {
  @Inject private NormalNamingStrategy delegate;
  @Inject private Options options;

  /**
   * Returns the name of the given model object, to be used as the name of an option.
   * @param e the given model object.
   * @return the name of the given model object, to be used as the name of an option.
   */
  @Override public Pair<NameType, String> nameOf(EObject e) {
    if (!(e instanceof IndexedElement)) {
      return delegate.nameOf(e);
    }
    String value = options.nameForOption((IndexedElement) e);
    if (isEmpty(value)) {
      return null;
    }
    return create(OPTION, value);
  }
}
