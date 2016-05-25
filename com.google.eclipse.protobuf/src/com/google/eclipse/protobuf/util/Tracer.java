/*
 * Copyright (c) 2016 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are
 * made available under the terms of the Eclipse Public License v1.0 which
 * accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.util;

import org.eclipse.osgi.service.debug.DebugOptions;
import org.eclipse.osgi.service.debug.DebugOptionsListener;
import org.eclipse.osgi.service.debug.DebugTrace;

/**
 * The debugging related arguments for the protobuf editor.
 *
 * @author atrookey@google.com (Alexander Rookey)
 */
public class Tracer {
  public static DebugTrace trace;
  public static boolean DEBUG_SCOPING = false;
  public static final String TRACE_PREFIX = "[Google Protobuf Editor] ";

  public static final DebugOptionsListener RESOURCES_DEBUG_OPTIONS_LISTENER =
      new DebugOptionsListener() {
        @Override
        public void optionsChanged(DebugOptions options) {
          if (trace == null) {
            trace = options.newDebugTrace("com.google.eclipse.protobuf.ui");
          }
          boolean debug = options.getBooleanOption("com.google.eclipse.protobuf.ui/debug", false);
          DEBUG_SCOPING =
              debug && options.getBooleanOption("com.google.eclipse.protobuf.ui/scoping", false);
        }
      };

  /**
   * Prints a trace message to standard output.
   */
  public static void trace(String message) {
    System.out.println(TRACE_PREFIX + message);
  }
}
