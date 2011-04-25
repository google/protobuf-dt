/*
 * Copyright (c) 2011 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.stubs;

import com.google.eclipse.protobuf.protobuf.impl.LiteralImpl;

/**
 * @author alruiz@google.com (Alex Ruiz)
 */
public class LiteralStub extends LiteralImpl implements EObjectStub {

  public LiteralStub(String name) {
    this.name = name;
  }

  public void setContainer(EContainerStub container) {
    this.eContainer = container;
  }
}
