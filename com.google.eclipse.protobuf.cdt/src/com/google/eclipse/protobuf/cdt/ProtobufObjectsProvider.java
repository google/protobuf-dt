/*
 * Copyright (c) 2012 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.cdt;

import com.google.eclipse.protobuf.ui.plugin.ProtobufEditorPlugIn;
import com.google.inject.Provider;

/**
 * Obtains objects from the protobuf editor plug-in.
 * @param <T> the type of object to obtain from the protobuf editor plug-in.
 *
 * @author alruiz@google.com (Alex Ruiz)
 */
class ProtobufObjectsProvider<T> implements Provider<T> {
  private final Class<T> type;

  static <T> ProtobufObjectsProvider<T> getfromProtobufPlugin(Class<T> type) {
    return new ProtobufObjectsProvider<T>(type);
  }

  private ProtobufObjectsProvider(Class<T> type) {
    this.type = type;
  }

  @Override public T get() {
    return ProtobufEditorPlugIn.getInstanceOf(type);
  }
}