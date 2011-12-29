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
 * A preference that stores a {@code String} value.
 *
 * @author alruiz@google.com (Alex Ruiz)
 */
public class StringPreference extends Preference<String> {
  /**
   * Creates a new </code>{@link StringPreference}</code>.
   * @param name the name of this preference.
   * @param store the store for this preference.
   */
  public StringPreference(String name, IPreferenceStore store) {
    super(name, store);
  }

  /** {@inheritDoc} */
  @Override public String getValue() {
    return getPreferenceStore().getString(getName());
  }

  /** {@inheritDoc} */
  @Override public String getDefaultValue() {
    return getPreferenceStore().getDefaultString(getName());
  }

  /** {@inheritDoc} */
  @Override public void setValue(String value) {
    getPreferenceStore().setValue(getName(), value);
  }

  /** {@inheritDoc} */
  @Override public void setDefaultValue(String value) {
    getPreferenceStore().setDefault(getName(), value);
  }
}
