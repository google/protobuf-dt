/*
 * Copyright (c) 2011 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.ui.plugin;

import com.google.eclipse.protobuf.ui.internal.ProtobufActivator;
import com.google.inject.Injector;

/**
 * @author alruiz@google.com (Alex Ruiz)
 */
public final class ProtobufEditorPlugIn {
  private static final String PLUGIN_ID = "com.google.eclipse.protobuf.ui";
  private static final String LANGUAGE_NAME = "com.google.eclipse.protobuf.Protobuf";

  /**
   * Returns the appropriate instance for the given injection type.
   * @param type the given injection type.
   * @return the appropriate instance for the given injection type.
   */
  public static <T> T getInstanceOf(Class<T> type) {
    return injector().getInstance(type);
  }

  /**
   * Returns the plug-in's injector for the 'Protocol Buffer' language.
   * @return the plug-in's injector for the 'Protocol Buffer' language.
   */
  public static Injector injector() {
    return ProtobufActivator.getInstance().getInjector(protobufLanguageName());
  }

  public static String protobufLanguageName() {
    return LANGUAGE_NAME;
  }

  public static String protobufPluginId() {
    return PLUGIN_ID;
  }

  private ProtobufEditorPlugIn() {}
}
