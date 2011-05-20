/*
 * Copyright (c) 2011 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.ui.preferences.compiler;

import static com.google.eclipse.protobuf.ui.preferences.compiler.PreferenceNames.*;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.xtext.ui.editor.preferences.IPreferenceStoreAccess;
import org.eclipse.xtext.ui.editor.preferences.IPreferenceStoreInitializer;

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
    store.setDefault(ENABLE_PROJECT_SETTINGS, false);
    store.setDefault(USE_PROTOC_IN_SYSTEM_PATH, true);
    store.setDefault(GENERATE_JAVA_CODE, false);
    store.setDefault(GENERATE_CPP_CODE, false);
    store.setDefault(GENERATE_PYTHON_CODE, false);
    store.setDefault(JAVA_OUTPUT_DIRECTORY, DEFAULT_OUTPUT_DIRECTORY);
    store.setDefault(CPP_OUTPUT_DIRECTORY, DEFAULT_OUTPUT_DIRECTORY);
    store.setDefault(PYTHON_OUTPUT_DIRECTORY, DEFAULT_OUTPUT_DIRECTORY);
    store.setDefault(REFRESH_RESOURCES, true);
    store.setDefault(REFRESH_OUTPUT_DIRECTORY, true);
  }

}
