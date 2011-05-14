/*
 * Copyright (c) 2011 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.ui.preferences.compiler;

import static com.google.eclipse.protobuf.ui.preferences.compiler.CompilerPreferenceNames.*;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.xtext.ui.editor.preferences.IPreferenceStoreAccess;
import org.eclipse.xtext.ui.editor.preferences.IPreferenceStoreInitializer;

/**
 * Initializes default values for the "Compiler" preferences.
 *
 * @author alruiz@google.com (Alex Ruiz)
 */
public class CompilerPreferenceStoreInitializer implements IPreferenceStoreInitializer {

  /** {@inheritDoc} */
  public void initialize(IPreferenceStoreAccess access) {
    IPreferenceStore store = access.getWritablePreferenceStore();
    store.setDefault(ENABLE_PROJECT_SETTINGS, false);
    store.setDefault(USE_PROTOC_IN_SYSTEM_PATH, true);
    store.setDefault(GENERATE_JAVA_CODE, true);
    store.setDefault(OUTPUT_FOLDER_NAME, "src-gen");
    store.setDefault(REFRESH_RESOURCES, true);
    store.setDefault(REFRESH_OUTPUT_FOLDER, true);
  }

}
