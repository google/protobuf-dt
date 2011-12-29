/*
 * Copyright (c) 2011 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.ui.preferences.general.core;

import org.eclipse.core.resources.IProject;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.xtext.ui.editor.preferences.IPreferenceStoreAccess;

import com.google.eclipse.protobuf.ui.preferences.BooleanPreference;

/**
 * General preferences, retrieved from an <code>{@link IPreferenceStore}</code>.
 *
 * @author alruiz@google.com (Alex Ruiz)
 */
public class GeneralPreferences {
  public static final String ENABLE_PROJECT_SETTINGS_PREFERENCE_NAME = "general.enableProjectSettings";

  /**
   * Creates a new <code>{@link GeneralPreferences}</code>.
   * @param storeAccess simplified access to Eclipse's preferences.
   * @param project the current project.
   * @return the created {@code GeneralPreferences}.
   */
  public static GeneralPreferences generalPreferences(IPreferenceStoreAccess storeAccess, IProject project) {
    IPreferenceStore store = storeAccess.getWritablePreferenceStore(project);
    boolean enableProjectSettings = store.getBoolean(ENABLE_PROJECT_SETTINGS_PREFERENCE_NAME);
    if (!enableProjectSettings) {
      store = storeAccess.getWritablePreferenceStore();
    }
    return new GeneralPreferences(store);
  }

  private final BooleanPreference enableProjectSettings;
  private final BooleanPreference validateFilesOnActivation;

  /**
   * Creates a new <code>{@link GeneralPreferences}</code>.
   * @param store a table mapping named preferences to values.
   */
  public GeneralPreferences(IPreferenceStore store) {
    enableProjectSettings = new BooleanPreference(ENABLE_PROJECT_SETTINGS_PREFERENCE_NAME, store);
    validateFilesOnActivation = new BooleanPreference("general.validateFilesOnActivation", store);
  }

  /**
   * Returns the setting that specifies whether project project should be used instead of workspace preferences.
   * @return the setting that specifies whether project project should be used instead of workspace preferences.
   */
  public BooleanPreference enableProjectSettings() {
    return enableProjectSettings;
  }

  /**
   * Returns the setting that specifies whether files should be validated when activated.
   * @return the setting that specifies whether files should be validated when activated.
   */
  public BooleanPreference validateFilesOnActivation() {
    return validateFilesOnActivation;
  }
}