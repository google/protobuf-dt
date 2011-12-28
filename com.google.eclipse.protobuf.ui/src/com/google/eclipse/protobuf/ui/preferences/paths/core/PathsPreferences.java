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

import com.google.eclipse.protobuf.ui.preferences.*;
import com.google.eclipse.protobuf.ui.preferences.editor.save.core.SaveActionsPreferences;

/**
 * "Paths" preferences, retrieved from an <code>{@link IPreferenceStore}</code>.
 *
 * @author alruiz@google.com (Alex Ruiz)
 */
public class PathsPreferences {

  private final BooleanPreference filesInOneDirectoryOnly;
  private final BooleanPreference filesInMultipleDirectories;
  private final StringPreference directoryPaths;

  /**
   * Creates a new <code>{@link SaveActionsPreferences}</code>.
   * @param store a table mapping named preferences to values.
   */
  public PathsPreferences(IPreferenceStore store) {
    filesInOneDirectoryOnly = new BooleanPreference("paths.filesInOneDirectoryOnly", store);
    filesInMultipleDirectories = new BooleanPreference("paths.filesInMultipleDirectories", store);
    directoryPaths = new StringPreference("paths.directoryPaths", store);
  }

  /**
   * Returns the setting that specifies whether all the .proto files are stored in one directory.
   * @return the setting that specifies whether all the .proto files are stored in one directory.
   */
  public BooleanPreference filesInOneDirectoryOnly() {
    return filesInOneDirectoryOnly;
  }

  /**
   * Returns the setting that specifies whether all the .proto files are stored in multiple directories.
   * @return the setting that specifies whether all the .proto files are stored in multiple directories.
   */
  public BooleanPreference filesInMultipleDirectories() {
    return filesInMultipleDirectories;
  }

  /**
   * Returns the setting that specifies a CSV {@code String} containing the paths of the directories that store .proto
   * files.
   * @return the setting that specifies a CSV {@code String} containing the paths of the directories that store .proto
   * files.
   */
  public StringPreference directoryPaths() {
    return directoryPaths;
  }
}
