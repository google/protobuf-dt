/*
 * Copyright (c) 2011 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.ui.preferences.pages.compiler;

import org.eclipse.core.resources.IProject;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.xtext.ui.editor.preferences.IPreferenceStoreAccess;

import com.google.inject.Inject;

/**
 * Reads compiler preferences.
 *
 * @author alruiz@google.com (Alex Ruiz)
 */
public class CompilerPreferencesProvider {

  @Inject private IPreferenceStoreAccess storeAccess;

  public CompilerPreferences getPreferences(IProject project) {
    IPreferenceStore store = storeAccess.getWritablePreferenceStore(project);
    RawPreferences preferences = new RawPreferences(store);
    boolean useProjectPreferences = preferences.enableProjectSettings().value();
    if (!useProjectPreferences) store = storeAccess.getWritablePreferenceStore();
    return new CompilerPreferences(new RawPreferences(store));
  }
}
