/*
 * Copyright (c) 2011 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.ui.preferences.pages.compiler;

import static com.google.eclipse.protobuf.ui.preferences.pages.compiler.EnableProjectSettingsPreference.enableProjectSettings;

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
  public void initialize(IPreferenceStoreAccess access) {
    IPreferenceStore store = access.getWritablePreferenceStore();
    enableProjectSettings(store).defaultValue(false);
    RawPreferences preferences = new RawPreferences(store);
    preferences.useProtocInSystemPath().defaultValue(true);
    preferences.javaCodeGenerationEnabled().defaultValue(false);
    preferences.cppCodeGenerationEnabled().defaultValue(false);
    preferences.pythonCodeGenerationEnabled().defaultValue(false);
    preferences.javaOutputDirectory().defaultValue(DEFAULT_OUTPUT_DIRECTORY);
    preferences.cppOutputDirectory().defaultValue(DEFAULT_OUTPUT_DIRECTORY);
    preferences.pythonOutputDirectory().defaultValue(DEFAULT_OUTPUT_DIRECTORY);
    preferences.refreshResources().defaultValue(true);
    preferences.refreshOutputDirectory().defaultValue(true);
  }
}
