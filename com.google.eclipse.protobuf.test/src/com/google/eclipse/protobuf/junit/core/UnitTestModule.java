/*
* Copyright (c) 2011 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.junit.core;

import static org.mockito.Mockito.mock;

import org.eclipse.core.runtime.IExtensionRegistry;

import com.google.inject.AbstractModule;
import com.google.inject.Provider;

/**
 * Guice module for unit testing.
 *
 * @author alruiz@google.com (Alex Ruiz)
 */
public class UnitTestModule extends AbstractModule {
  public static UnitTestModule unitTestModule() {
    return new UnitTestModule();
  }

  private UnitTestModule( ) {}

  @Override protected void configure() {
    binder().bind(IExtensionRegistry.class).toProvider(ExtensionRegistryProvider.class);
  }

  private static class ExtensionRegistryProvider implements Provider<IExtensionRegistry> {
    @Override public IExtensionRegistry get() {
      return mock(IExtensionRegistry.class);
    }
  }
}
