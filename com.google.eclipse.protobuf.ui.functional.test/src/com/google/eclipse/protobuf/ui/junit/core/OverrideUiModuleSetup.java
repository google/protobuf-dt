/*
 * Copyright (c) 2011 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.ui.junit.core;

import static org.eclipse.xtext.util.Modules2.mixin;

import org.eclipse.xtext.ISetup;
import org.eclipse.xtext.ui.shared.SharedStateModule;

import com.google.eclipse.protobuf.*;
import com.google.eclipse.protobuf.junit.core.XtextRule;
import com.google.eclipse.protobuf.ui.ProtobufUiModule;
import com.google.eclipse.protobuf.ui.internal.ProtobufActivator;
import com.google.inject.*;

/**
 * @author alruiz@google.com (Alex Ruiz)
 */
public class OverrideUiModuleSetup extends ProtobufStandaloneSetup {
  private final Module module;

  public static XtextRule overrideUiModuleWith(Module module) {
    ISetup setup = new OverrideUiModuleSetup(module);
    return XtextRule.createWith(setup);
  }

  OverrideUiModuleSetup(Module module) {
    this.module = module;
  }

  @Override public Injector createInjector() {
    Module mixin = mixin(new ProtobufRuntimeModule(),
                         new SharedStateModule(),
                         new ProtobufUiModule(ProtobufActivator.getInstance()),
                         module);
    return Guice.createInjector(mixin);
  }
}
