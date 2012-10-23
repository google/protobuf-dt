/*
 * Copyright (c) 2012 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.cdt;

import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

import com.google.inject.Guice;
import com.google.inject.Injector;

/**
 * Controls the plug-in life cycle.
 *
 * @author alruiz@google.com (Alex Ruiz)
 */
public class ProtobufCdtActivator extends AbstractUIPlugin {
  public static final String PLUGIN_ID = "com.google.eclipse.protobuf.cdt";

  private static ProtobufCdtActivator plugin;

  private final Injector injector;

  public ProtobufCdtActivator() {
    injector = Guice.createInjector(new ProtobufCdtModule());
  }

  /**
   * Returns the singleton instance of this class.
   * @return the singleton instance of this class.
   */
  public static ProtobufCdtActivator getInstance() {
    return plugin;
  }

  @Override public void start(BundleContext context) throws Exception {
    super.start(context);
    plugin = this;
  }

  @Override public void stop(BundleContext context) throws Exception {
    plugin = null;
    super.stop(context);
  }

  public Injector getInjector() {
    return injector;
  }
}
