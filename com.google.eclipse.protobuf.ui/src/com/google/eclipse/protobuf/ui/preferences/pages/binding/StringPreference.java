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
 * A preference that stores a {@code String} value.
 *
 * @author alruiz@google.com (Alex Ruiz)
 */
class StringPreference extends AbstractPreference<String> {
  StringPreference(String name, IPreferenceStore store) {
    super(name, store);
  }

  @Override public String value() {
    return getPreferenceStore().getString(name());
  }

  @Override public String defaultValue() {
    return getPreferenceStore().getDefaultString(name());
  }

  @Override public void updateValue(String value) {
    getPreferenceStore().setValue(name(), value);
  }
}
