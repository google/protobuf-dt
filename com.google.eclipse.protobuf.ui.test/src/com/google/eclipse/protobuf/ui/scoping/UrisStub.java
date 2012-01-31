/*
 * Copyright (c) 2011 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.ui.scoping;

import com.google.eclipse.protobuf.ui.util.Uris;

import org.eclipse.emf.common.util.URI;

/**
 * @author alruiz@google.com (Alex Ruiz)
 */
class UrisStub extends Uris {
  private static final UrisStub INSTANCE = new UrisStub();

  static UrisStub instance() {
    return INSTANCE;
  }

  private boolean exist;

  private UrisStub() {}

  void shouldAnyUriExist(boolean shouldExist) {
    exist = shouldExist;
  }

  @Override public boolean exists(URI uri) {
    return exist;
  }
}
