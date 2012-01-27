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

import com.google.inject.*;

/**
 * Returns the name of a model object obtained from a <code>{@link NameResolver}</code>.
 *
 * @author alruiz@google.com (Alex Ruiz)
 */
@Singleton public class NormalNamingStrategy implements NamingStrategy {
  @Inject private NameResolver nameResolver;

  /** {@inheritDoc} */
  @Override public String nameOf(EObject e) {
    return nameResolver.nameOf(e);
  }
}
