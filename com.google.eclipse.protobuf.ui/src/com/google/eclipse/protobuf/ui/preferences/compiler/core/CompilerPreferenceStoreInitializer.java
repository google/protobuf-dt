/*
 * Copyright (c) 2011 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.ui.preferences.compiler.core;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.xtext.ui.editor.preferences.*;

/**
 * Initializes default values for the "Compiler" preferences.
 *
 * @author alruiz@google.com (Alex Ruiz)
 */
public class CompilerPreferenceStoreInitializer implements IPreferenceStoreInitializer {

  private static final String DEFAULT_OUTPUT_DIRECTORY = "src-gen";

  /** {@inheritDoc} */
  @Override public void initialize(IPreferenceStoreAccess access) {
    IPreferenceStore store = access.getWritablePreferenceStore();
    CompilerPreferences preferences = new CompilerPreferences(store);
    preferences.enableProjectSettings().setDefaultValue(false);
    preferences.useProtocInSystemPath().setDefaultValue(true);
    preferences.javaCodeGenerationEnabled().setDefaultValue(false);
    preferences.cppCodeGenerationEnabled().setDefaultValue(false);
    preferences.pythonCodeGenerationEnabled().setDefaultValue(false);
    preferences.javaOutputDirectory().setDefaultValue(DEFAULT_OUTPUT_DIRECTORY);
    preferences.cppOutputDirectory().setDefaultValue(DEFAULT_OUTPUT_DIRECTORY);
    preferences.pythonOutputDirectory().setDefaultValue(DEFAULT_OUTPUT_DIRECTORY);
    preferences.refreshResources().setDefaultValue(true);
    preferences.refreshOutputDirectory().setDefaultValue(true);
  }
}
