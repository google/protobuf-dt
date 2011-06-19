/*
 * Copyright (c) 2011 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.ui.preferences.pages.paths;

import org.eclipse.jface.preference.IPreferenceStore;

import com.google.eclipse.protobuf.ui.preferences.*;

/**
 * @author alruiz@google.com (Alex Ruiz)
 */
class RawPreferences {

  private final BooleanPreference filesInOneDirectoryOnly;
  private final BooleanPreference filesInMultipleDirectories;
  private final StringPreference directoryPaths;

  RawPreferences(IPreferenceStore store) {
    filesInOneDirectoryOnly = new BooleanPreference("paths.filesInOneDirectoryOnly", store);
    filesInMultipleDirectories = new BooleanPreference("paths.filesInMultipleDirectories", store);
    directoryPaths = new StringPreference("paths.directoryPaths", store);
  }

  BooleanPreference filesInOneDirectoryOnly() {
    return filesInOneDirectoryOnly;
  }

  BooleanPreference filesInMultipleDirectories() {
    return filesInMultipleDirectories;
  }

  StringPreference directoryPaths() {
    return directoryPaths;
  }
}
