/*
 * Copyright (c) 2011 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.ui.preferences.paths.core;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.xtext.ui.editor.preferences.*;

/**
 * Initializes default values for the "Paths" preferences.
 *
 * @author alruiz@google.com (Alex Ruiz)
 */
public class PathsPreferenceStoreInitializer implements IPreferenceStoreInitializer {

  /** {@inheritDoc} */
  @Override public void initialize(IPreferenceStoreAccess access) {
    IPreferenceStore store = access.getWritablePreferenceStore();
    PathsPreferences preferences = new PathsPreferences(store);
    preferences.filesInOneDirectoryOnly().setDefaultValue(true);
    preferences.filesInMultipleDirectories().setDefaultValue(false);
    preferences.directoryPaths().setDefaultValue("");
  }
}
