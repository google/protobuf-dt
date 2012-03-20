/*
 * Copyright (c) 2011 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.ui.preferences.pages.binding;

import org.eclipse.jface.preference.IPreferenceStore;

/**
 * Base class for implementations of <code>{@link Preference}</code>.
 * @param <T> the type of value this preference handles.
 *
 * @author alruiz@google.com (Alex Ruiz)
 */
public abstract class AbstractPreference<T> implements Preference<T> {
  private final String name;
  private final IPreferenceStore store;

  public AbstractPreference(String name, IPreferenceStore store) {
    this.name = name;
    this.store = store;
  }

  @Override public String name() {
    return name;
  }

  protected IPreferenceStore getPreferenceStore() {
    return store;
  }
}
