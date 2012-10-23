/*
 * Copyright (c) 2012 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.ui.preferences.compiler;

import static com.google.eclipse.protobuf.ui.preferences.compiler.PreferenceNames.PYTHON_CODE_GENERATION_ENABLED;
import static com.google.eclipse.protobuf.ui.preferences.compiler.PreferenceNames.PYTHON_OUTPUT_DIRECTORY;

import org.eclipse.core.resources.IProject;
import org.eclipse.jface.preference.IPreferenceStore;

/**
 * @author alruiz@google.com (Alex Ruiz)
 */
class PythonCodeGenerationPreference implements CodeGenerationPreference {
  private final IPreferenceStore store;
  private final IProject project;

  PythonCodeGenerationPreference(IPreferenceStore store, IProject project) {
    this.store = store;
    this.project = project;
  }

  @Override public boolean isEnabled() {
    return store.getBoolean(PYTHON_CODE_GENERATION_ENABLED);
  }

  @Override public String outputDirectory() {
    return store.getString(PYTHON_OUTPUT_DIRECTORY);
  }

  @Override public IProject project() {
    return project;
  }
}
