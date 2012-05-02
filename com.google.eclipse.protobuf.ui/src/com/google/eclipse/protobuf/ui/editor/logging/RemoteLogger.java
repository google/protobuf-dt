/*
 * Copyright (c) 2012 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.ui.editor.logging;

import static java.util.logging.Level.SEVERE;

import com.google.inject.*;

import org.eclipse.core.runtime.*;

import java.util.logging.Logger;

/**
 * @author alruiz@google.com (Alex Ruiz)
 */
@Singleton public class RemoteLogger {
  private static final String EXTENSION_ID = "com.google.eclipse.protobuf.ui.remoteLogging";

  private static Logger logger = Logger.getLogger(RemoteLogger.class.getCanonicalName());

  @Inject private IExtensionRegistry registry;

  private boolean initialized;
  private EditorUsageLogger editorUsageLogger;

  private final Object lock = new Object();

  public EditorUsageLogger editorUsage() {
    synchronized (lock) {
      if (!initialized) {
        initialize();
      }
      return editorUsageLogger;
    }
  }

  private void initialize() {
    initialized = true;
    IConfigurationElement[] config = registry.getConfigurationElementsFor(EXTENSION_ID);
    if (config == null) {
      return;
    }
    for (IConfigurationElement e : config) {
      if ("editorUsage".equals(e.getName()) && editorUsageLogger == null) {
        try {
          editorUsageLogger = (EditorUsageLogger) e.createExecutableExtension("class");
        } catch (CoreException error) {
          logger.log(SEVERE, "Unable to create 'editorUsage' instance", error);
        }
      }
    }
  }
}
