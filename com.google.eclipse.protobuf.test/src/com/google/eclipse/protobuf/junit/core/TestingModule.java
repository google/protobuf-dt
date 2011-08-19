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

import com.google.eclipse.protobuf.ProtobufRuntimeModule;
import com.google.inject.Binder;
import com.google.inject.Provider;

/**
 * @author alruiz@google.com (Alex Ruiz)
 */
public class TestingModule extends ProtobufRuntimeModule {

  @Override public void configureExtensionRegistry(Binder binder) {
    binder.bind(IExtensionRegistry.class).toProvider(FakeExtensionRegistryProvider.class);    
  }
  
  public static class FakeExtensionRegistryProvider implements Provider<IExtensionRegistry> {
    public IExtensionRegistry get() {
      return mock(IExtensionRegistry.class);
    }
  }
}
