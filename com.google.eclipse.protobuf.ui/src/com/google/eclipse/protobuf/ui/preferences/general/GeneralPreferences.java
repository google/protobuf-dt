/*
 * Copyright (c) 2011 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.ui.preferences.general;

import static com.google.eclipse.protobuf.ui.preferences.general.PreferenceNames.ENABLE_PROJECT_SETTINGS_PREFERENCE_NAME;
import static com.google.eclipse.protobuf.ui.preferences.general.PreferenceNames.VALIDATE_FILES_ON_ACTIVATION;

import org.eclipse.core.resources.IProject;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.xtext.ui.editor.preferences.IPreferenceStoreAccess;
import org.eclipse.xtext.ui.editor.preferences.IPreferenceStoreInitializer;

/**
 * @author alruiz@google.com (Alex Ruiz)
 */
public class GeneralPreferences {
  private final IPreferenceStore store;

  public static GeneralPreferences generalPreferences(IPreferenceStoreAccess storeAccess, IProject project) {
    IPreferenceStore store = storeAccess.getWritablePreferenceStore(project);
    boolean enableProjectSettings = store.getBoolean(ENABLE_PROJECT_SETTINGS_PREFERENCE_NAME);
    if (!enableProjectSettings) {
      store = storeAccess.getWritablePreferenceStore();
    }
    return new GeneralPreferences(store);
  }

  private GeneralPreferences(IPreferenceStore store) {
    this.store = store;
  }

  public boolean shouldValidateFilesOnActivation() {
    return store.getBoolean(VALIDATE_FILES_ON_ACTIVATION);
  }

  public static class Initializer implements IPreferenceStoreInitializer {
    @Override public void initialize(IPreferenceStoreAccess storeAccess) {
      IPreferenceStore store = storeAccess.getWritablePreferenceStore();
      store.setDefault(ENABLE_PROJECT_SETTINGS_PREFERENCE_NAME, false);
      store.setDefault(VALIDATE_FILES_ON_ACTIVATION, true);
    }
  }
}