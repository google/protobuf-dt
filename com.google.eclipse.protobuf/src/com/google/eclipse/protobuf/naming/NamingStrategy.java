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

/**
 * Knows how to obtain the name of a model object.
 *
 * @author alruiz@google.com (Alex Ruiz)
 */
public interface NamingStrategy {
  /**
   * Returns the name of the given model object.
   * @param e the given model object.
   * @return the name of the given model object.
   */
  String nameOf(EObject e);
}
