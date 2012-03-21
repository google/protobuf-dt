/*
 * Copyright (c) 2012 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.ui.preferences.compiler;

import static com.google.eclipse.protobuf.ui.preferences.compiler.PreferenceNames.*;

import org.eclipse.jface.preference.IPreferenceStore;

/**
 * @author alruiz@google.com (Alex Ruiz)
 */
class CppCodeGenerationPreference implements CodeGenerationPreference {
  private final IPreferenceStore store;

  CppCodeGenerationPreference(IPreferenceStore store) {
    this.store = store;
  }

  @Override public boolean isEnabled() {
    return store.getBoolean(JAVA_CODE_GENERATION_ENABLED);
  }

  @Override public String outputDirectory() {
    return store.getString(JAVA_OUTPUT_DIRECTORY);
  }
}
