/*
 * Copyright (c) 2011 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.preferences.general;

import static com.google.eclipse.protobuf.preferences.general.PreferenceNames.ENABLE_PROJECT_SETTINGS_PREFERENCE_NAME;
import static com.google.eclipse.protobuf.preferences.general.PreferenceNames.VALIDATE_FILES_ON_ACTIVATION;

import com.google.eclipse.protobuf.preferences.DefaultPreservingInitializer;

import org.eclipse.core.resources.IProject;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.xtext.ui.editor.preferences.IPreferenceStoreAccess;

/** @author alruiz@google.com (Alex Ruiz) */
public class GeneralPreferences {
  private final IPreferenceStore store;

  public GeneralPreferences(IPreferenceStoreAccess storeAccess, IProject project) {
    IPreferenceStore preferenceStore = storeAccess.getWritablePreferenceStore(project);
    if (!preferenceStore.getBoolean(ENABLE_PROJECT_SETTINGS_PREFERENCE_NAME)) {
      preferenceStore = storeAccess.getWritablePreferenceStore();
    }
    this.store = preferenceStore;
  }

  public boolean shouldValidateFilesOnActivation() {
    return store.getBoolean(VALIDATE_FILES_ON_ACTIVATION);
  }

  public String getDescriptorProtoPath() {
    return store.getString(PreferenceNames.DESCRIPTOR_PROTO_PATH);
  }

  public void addPropertyChangeListener(IPropertyChangeListener listener) {
    store.addPropertyChangeListener(listener);
  }

  public static class Initializer extends DefaultPreservingInitializer {
    @Override
    public void setDefaults() {
      setDefault(ENABLE_PROJECT_SETTINGS_PREFERENCE_NAME, false);
      setDefault(VALIDATE_FILES_ON_ACTIVATION, true);
      setDefault(PreferenceNames.DESCRIPTOR_PROTO_PATH, PreferenceNames.DEFAULT_DESCRIPTOR_PATH);
    }
  }
}
