/*
 * Copyright (c) 2011 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.ui.preferences.parser;

import org.eclipse.jface.preference.IPreferenceStore;

import com.google.eclipse.protobuf.ui.preferences.BooleanPreference;

/**
 * @author alruiz@google.com (Alex Ruiz)
 */
class RawPreferences {

  private final BooleanPreference enableProto2OnlyChecks;

  RawPreferences(IPreferenceStore store) {
    enableProto2OnlyChecks = new BooleanPreference("compiler.checkProto2", store);
  }

  BooleanPreference enableProto2OnlyChecks() {
    return enableProto2OnlyChecks;
  }
}
