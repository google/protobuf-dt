/*
 * Copyright (c) 2011 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.junit.model.find;

import org.eclipse.emf.ecore.EObject;

public final class Root {

  final EObject value;

  public static Root in(EObject value) {
    return new Root(value);
  }

  private Root(EObject value) {
    this.value = value;
  }
}
