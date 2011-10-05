/*
 * Copyright (c) 2011 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.junit.core;

import org.eclipse.xtext.ISetup;

import com.google.eclipse.protobuf.ProtobufStandaloneSetup;

/**
 * @author alruiz@google.com (Alex Ruiz)
 */
public final class Setups {

  public static ISetup unitTestSetup() {
    return new TestingStandaloneSetup();
  }
  
  public static ISetup integrationTestSetup() {
    return new ProtobufStandaloneSetup();
  }
  
  private Setups() {}
}
