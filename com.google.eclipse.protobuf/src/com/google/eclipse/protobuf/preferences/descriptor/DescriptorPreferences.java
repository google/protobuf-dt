/*
 * Copyright (c) 2014 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.preferences.descriptor;

import org.eclipse.core.resources.IProject;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.xtext.ui.editor.preferences.IPreferenceStoreAccess;
import org.eclipse.xtext.ui.editor.preferences.IPreferenceStoreInitializer;

/**
 * Preferences for project-level descriptor proto.
 */
public class DescriptorPreferences {
  private final IPreferenceStore store;

  public DescriptorPreferences(IPreferenceStoreAccess storeAccess, IProject project) {
    this.store = storeAccess.getWritablePreferenceStore(project);
  }

  public String getDescriptorProtoPath() {
    return store.getString(PreferenceNames.DESCRIPTOR_PROTO_PATH);
  }

  public static class Initializer implements IPreferenceStoreInitializer {
    @Override public void initialize(IPreferenceStoreAccess access) {
      IPreferenceStore store = access.getWritablePreferenceStore();
      store.setDefault(PreferenceNames.DESCRIPTOR_PROTO_PATH,
          PreferenceNames.DEFAULT_DESCRIPTOR_PATH);
    }
  }
}
