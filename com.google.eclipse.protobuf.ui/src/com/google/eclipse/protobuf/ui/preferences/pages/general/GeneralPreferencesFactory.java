/*
 * Copyright (c) 2011 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.ui.preferences.pages.general;

import static com.google.eclipse.protobuf.ui.preferences.pages.general.EnableProjectSettingsPreference.enableProjectSettings;

import org.eclipse.core.resources.IProject;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.xtext.ui.editor.preferences.IPreferenceStoreAccess;

import com.google.inject.Inject;

/**
 * Factory of <code>{@link GeneralPreferences}</code>.
 *
 * @author alruiz@google.com (Alex Ruiz)
 */
public class GeneralPreferencesFactory {

  @Inject private IPreferenceStoreAccess storeAccess;

  public GeneralPreferences preferences(IProject project) {
    IPreferenceStore store = storeAccess.getWritablePreferenceStore(project);
    boolean useProjectPreferences = enableProjectSettings(store).value();
    if (!useProjectPreferences) {
      store = storeAccess.getWritablePreferenceStore();
    }
    return new GeneralPreferences(new RawPreferences(store));
  }
}
