/*
 * Copyright (c) 2011 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.junit.core;

import com.google.eclipse.protobuf.ProtobufStandaloneSetup;
import com.google.inject.Guice;
import com.google.inject.Injector;

/**
 * @author alruiz@google.com (Alex Ruiz)
 */
public class TestingStandaloneSetup extends ProtobufStandaloneSetup {

  @Override
  public Injector createInjector() {
    return Guice.createInjector(new TestingModule());
  }
}
