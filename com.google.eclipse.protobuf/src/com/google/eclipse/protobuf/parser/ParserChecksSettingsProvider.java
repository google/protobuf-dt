/*
 * Copyright (c) 2011 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.parser;

import org.eclipse.core.runtime.*;

import com.google.inject.*;

/**
 * Provider of settings of parser checks.
 *
 * @author alruiz@google.com (Alex Ruiz)
 */
@Singleton public class ParserChecksSettingsProvider {
  private static final String EXTENSION_ID = "com.google.eclipse.protobuf.parserChecks";

  @Inject private IExtensionRegistry registry;

  private boolean initialized;
  private boolean proto2OnlyCheck;

  private final Object lock = new Object();

  /**
   * Indicates whether the parser should ensure .proto files have "proto2" syntax.
   * @return {@code true} if the parser should ensure .proto files have "proto2" syntax, {@code false} otherwise.
   */
  public boolean shouldCheckProto2Only() {
    ensureIsInitialized();
    return proto2OnlyCheck;
  }

  private void ensureIsInitialized() {
    synchronized (lock) {
      if (initialized) {
        return;
      }
      initialized = true;
      initializeFromExtensionPoint();
    }
  }

  private void initializeFromExtensionPoint() {
    IConfigurationElement[] config = registry.getConfigurationElementsFor(EXTENSION_ID);
    if (config == null) {
      return;
    }
    for (IConfigurationElement e : config) {
      String proto2OnlyCheckAttribure = e.getAttribute("proto2OnlyCheck");
      proto2OnlyCheck = Boolean.parseBoolean(proto2OnlyCheckAttribure);
    }
  }
}
