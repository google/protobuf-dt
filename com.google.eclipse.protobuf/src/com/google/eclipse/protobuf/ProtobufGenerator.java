/*
 * Copyright (c) 2011 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf;

import org.eclipse.xtext.XtextRuntimeModule;
import org.eclipse.xtext.XtextStandaloneSetup;
import org.eclipse.xtext.generator.Generator;
import org.eclipse.xtext.xtext.ecoreInference.IXtext2EcorePostProcessor;

import com.google.inject.Guice;
import com.google.inject.Injector;

/**
 * @author alruiz@google.com (Alex Ruiz)
 */
@SuppressWarnings("restriction")
public class ProtobufGenerator extends Generator {
  public ProtobufGenerator() {
    new XtextStandaloneSetupExtension().createInjectorAndDoEMFRegistration();
  }
  
  private static class XtextStandaloneSetupExtension extends XtextStandaloneSetup {
    @Override public Injector createInjector() {
      return Guice.createInjector(new XtextRuntimeModuleExtension());
    }
  }
  
  private static class XtextRuntimeModuleExtension extends XtextRuntimeModule {
    @Override public Class<? extends IXtext2EcorePostProcessor> bindIXtext2EcorePostProcessor() {
      return ProtobufEcorePostProcessor.class;
    }
  }
}
