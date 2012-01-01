/*
 * Copyright (c) 2011 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.junit.core;

import static org.eclipse.xtext.util.Modules2.mixin;

import com.google.eclipse.protobuf.*;
import com.google.inject.*;


/**
 * @author alruiz@google.com (Alex Ruiz)
 */
public class OverrideRuntimeModuleSetup extends ProtobufStandaloneSetup {
  private final Module module;

  OverrideRuntimeModuleSetup(Module module) {
    this.module = module;
  }

  @Override public Injector createInjector() {
    Module mixin = mixin(new ProtobufRuntimeModule(), module);
    return Guice.createInjector(mixin);
  }
}
