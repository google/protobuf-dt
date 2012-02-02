/*
 * Copyright (c) 2011 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.ui.util;

import com.google.eclipse.protobuf.ui.internal.ProtobufActivator;
import com.google.inject.Injector;

/**
 * @author alruiz@google.com (Alex Ruiz)
 */
public final class ProtobufPlugIn {
  private static final String LANGUAGE_NAME = "com.google.eclipse.protobuf.Protobuf";

  public static <T> T getInstance(Class<T> type) {
    return injector().getInstance(type);
  }

  public static Injector injector() {
    return ProtobufActivator.getInstance().getInjector(languageName());
  }

  public static String languageName() {
    return LANGUAGE_NAME;
  }

  private ProtobufPlugIn() {}
}
