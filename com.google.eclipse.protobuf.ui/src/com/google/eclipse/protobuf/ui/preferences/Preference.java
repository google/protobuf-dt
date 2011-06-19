/*
 * Copyright (c) 2011 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.ui.preferences;

import org.eclipse.jface.preference.IPreferenceStore;

/**
 * A single preference value.
 * @param <T> the type of value this preference handles.
 *
 * @author alruiz@google.com (Alex Ruiz)
 */
public abstract class Preference<T> {

  protected final String name;
  protected final IPreferenceStore store;

  /**
   * Creates a new </code>{@link Preference}</code>.
   * @param name the name of this preference.
   * @param store the store for this preference.
   */
  public Preference(String name, IPreferenceStore store) {
    this.name = name;
    this.store = store;
  }

  /**
   * Returns the name of this preference.
   * @return the name of this preference.
   */
  public final String name() {
    return name;
  }

  /**
   * Returns the value of this preference.
   * @return the value of this preference.
   */
  public abstract T value();

  /**
   * Returns the default value of this preference.
   * @return the default value of this preference.
   */
  public abstract T defaultValue();

  /**
   * Saves the value of this preference to the given store.
   * @param value the value to save.
   */
  public abstract void value(T value);

  /**
   * Saves the default value of this preference to the given store.
   * @param value the default value to save.
   */
  public abstract void defaultValue(T value);
}
