/*
 * Copyright (c) 2011 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.ui.preferences.pages.compiler;

import org.eclipse.jface.preference.IPreferenceStore;

import com.google.eclipse.protobuf.ui.preferences.BooleanPreference;

/**
 * @author alruiz@google.com (Alex Ruiz)
 */
class EnableProjectSettingsPreference {

  static BooleanPreference enableProjectSettings(IPreferenceStore store) {
    return new BooleanPreference("compiler.enableProjectSettings", store);
  }

  private EnableProjectSettingsPreference() {}
}
