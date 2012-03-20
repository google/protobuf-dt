/*
 * Copyright (c) 2011 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.ui.preferences.pages.binding;

/**
 * A single preference value.
 * @param <T> the type of value this preference handles.
 *
 * @author alruiz@google.com (Alex Ruiz)
 */
public interface Preference<T> {
  String name();

  T value();

  T defaultValue();

  void updateValue(T value);
}
