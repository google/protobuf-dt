/*
 * Copyright (c) 2012 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.cdt;

import org.eclipse.xtext.ui.guice.AbstractGuiceAwareExecutableExtensionFactory;
import org.osgi.framework.Bundle;

import com.google.inject.Injector;

/**
 * @author alruiz@google.com (Alex Ruiz)
 */
public class ProtobufCdtExecutableExtensionFactory extends AbstractGuiceAwareExecutableExtensionFactory {

  @Override protected Bundle getBundle() {
    return plugIn().getBundle();
  }

  @Override protected Injector getInjector() {
    return plugIn().getInjector();
  }

  private ProtobufCdtActivator plugIn() {
    return ProtobufCdtActivator.getInstance();
  }
}
