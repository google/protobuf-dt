/*
 * Copyright (c) 2012 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.ui.preferences.compiler;

import org.eclipse.jface.preference.IPreferenceStore;

/**
 * @author alruiz@google.com (Alex Ruiz)
 */
public class CodeGenerationPreference {
  private final IPreferenceStore store;
  private final String enabledPreferenceName;
  private final String outputDirectoryPreferenceName;

  CodeGenerationPreference(IPreferenceStore store, String enabledPreferenceName, String outputDirectoryPreferenceName) {
    this.store = store;
    this.enabledPreferenceName = enabledPreferenceName;
    this.outputDirectoryPreferenceName = outputDirectoryPreferenceName;
  }

  public boolean isEnabled() {
    return store.getBoolean(enabledPreferenceName);
  }

  public String outputDirectory() {
    return store.getString(outputDirectoryPreferenceName);
  }
}
