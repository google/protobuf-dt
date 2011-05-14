/*
 * Copyright (c) 2011 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.ui.preferences.paths;

import static com.google.eclipse.protobuf.ui.preferences.paths.PathsPreferenceNames.*;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.xtext.ui.editor.preferences.IPreferenceStoreAccess;
import org.eclipse.xtext.ui.editor.preferences.IPreferenceStoreInitializer;

/**
 * Initializes default values for the "Paths" preferences.
 *
 * @author alruiz@google.com (Alex Ruiz)
 */
public class PathsPreferenceStoreInitializer implements IPreferenceStoreInitializer {

  /** {@inheritDoc} */
  public void initialize(IPreferenceStoreAccess access) {
    IPreferenceStore store = access.getWritablePreferenceStore();
    store.setDefault(FILES_IN_ONE_DIRECTORY_ONLY, true);
    store.setDefault(FILES_IN_MULTIPLE_DIRECTORIES, false);
    store.setDefault(DIRECTORY_NAMES, "");
  }

}
