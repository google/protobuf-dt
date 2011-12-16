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

/**
 * @author alruiz@google.com (Alex Ruiz)
 */
public final class Setups {

  public static ISetup unitTestSetup() {
    return new UnitTestSetup();
  }

  public static ISetup integrationTestSetup() {
    return new IntegrationTestSetup();
  }

  private Setups() {}
}
