/*
 * Copyright (c) 2016 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are
 * made available under the terms of the Eclipse Public License v1.0 which
 * accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.ui.internal;

import java.util.Hashtable;

import org.eclipse.osgi.service.debug.DebugOptions;
import org.eclipse.osgi.service.debug.DebugOptionsListener;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;

import com.google.eclipse.protobuf.util.Tracer;

/**
 * The activator class controls the plug-in life cycle.
 *
 * @author atrookey@google.com (Alexander Rookey)
 */
public class Activator extends ProtobufActivator {
  private ServiceRegistration<DebugOptionsListener> debugOptionsListener;

  @Override
  public void start(BundleContext bundleContext) throws Exception {
    Hashtable<String, String> props = new Hashtable<>(4);
    props.put(DebugOptions.LISTENER_SYMBOLICNAME, "com.google.eclipse.protobuf.ui");
    debugOptionsListener =
        bundleContext.registerService(
            DebugOptionsListener.class, Tracer.RESOURCES_DEBUG_OPTIONS_LISTENER, props);
    super.start(bundleContext);
  }

  @Override
  public void stop(BundleContext bundleContext) throws Exception {
    try {
      debugOptionsListener.unregister();
    } finally {
      super.stop(bundleContext);
    }
  }
}
