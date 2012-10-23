/*
 * Copyright (c) 2011 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.junit.core;

import static java.util.Arrays.copyOf;

import com.google.eclipse.protobuf.ProtobufRuntimeModule;
import com.google.eclipse.protobuf.ProtobufStandaloneSetup;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;
import com.google.inject.util.Modules;

/**
 * @author alruiz@google.com (Alex Ruiz)
 */
public class OverrideRuntimeModuleSetup extends ProtobufStandaloneSetup {
  private final Module[] modules;

  public OverrideRuntimeModuleSetup(Module[] modules) {
    this.modules = copyOf(modules, modules.length);
  }

  @Override public Injector createInjector() {
    Module current = new ProtobufRuntimeModule();
    for (Module module : modules) {
      current = Modules.override(current).with(module);
    }
    return Guice.createInjector(current);
  }
}
