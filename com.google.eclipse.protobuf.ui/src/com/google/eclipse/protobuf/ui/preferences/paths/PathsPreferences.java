/*
 * Copyright (c) 2011 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.ui.preferences.paths;

import static com.google.eclipse.protobuf.ui.preferences.paths.DirectoryPath.parse;
import static com.google.eclipse.protobuf.ui.preferences.paths.PreferenceNames.DIRECTORY_PATHS;
import static com.google.eclipse.protobuf.ui.preferences.paths.PreferenceNames.FILES_IN_MULTIPLE_DIRECTORIES;
import static com.google.eclipse.protobuf.ui.preferences.paths.PreferenceNames.FILES_IN_ONE_DIRECTORY_ONLY;
import static com.google.eclipse.protobuf.ui.util.CommaSeparatedValues.splitCsv;

import org.eclipse.core.resources.IProject;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.xtext.ui.editor.preferences.IPreferenceStoreAccess;
import org.eclipse.xtext.ui.editor.preferences.IPreferenceStoreInitializer;

import com.google.common.base.Function;

/**
 * "Paths" preferences, retrieved from an <code>{@link IPreferenceStore}</code>.
 *
 * @author alruiz@google.com (Alex Ruiz)
 */
public class PathsPreferences {
  private final IProject project;
  private final IPreferenceStore store;

  public PathsPreferences(IPreferenceStoreAccess storeAccess, IProject project) {
    this.store = storeAccess.getWritablePreferenceStore(project);
    this.project = project;
  }

  public boolean areFilesInMultipleDirectories() {
    return store.getBoolean(FILES_IN_MULTIPLE_DIRECTORIES);
  }

  public <T> T applyToEachDirectoryPath(Function<DirectoryPath, T> function) {
    String directoryPaths = store.getString(DIRECTORY_PATHS);
    for (String s : splitCsv(directoryPaths)) {
      DirectoryPath path = parse(s, project);
      T value = function.apply(path);
      if (value != null) {
        return value;
      }
    }
    return null;
  }

  public static class Initializer implements IPreferenceStoreInitializer {
    @Override public void initialize(IPreferenceStoreAccess access) {
      IPreferenceStore store = access.getWritablePreferenceStore();
      store.setDefault(FILES_IN_ONE_DIRECTORY_ONLY, true);
      store.setDefault(FILES_IN_MULTIPLE_DIRECTORIES, false);
      store.setDefault(DIRECTORY_PATHS, "");
    }
  }
}
