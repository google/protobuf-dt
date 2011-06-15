/*
 * Copyright (c) 2011 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.ui.preferences.paths;

import static com.google.eclipse.protobuf.ui.preferences.paths.PathResolutionType.SINGLE_DIRECTORY;
import static com.google.eclipse.protobuf.ui.preferences.paths.PreferenceNames.IMPORT_ROOTS;
import static com.google.eclipse.protobuf.ui.util.Strings.CSV_PATTERN;
import static java.util.Collections.*;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.preference.IPreferenceStore;

/**
 * Paths preferences, retrieved from an <code>{@link IPreferenceStore}</code>. To create a new instance invoke
 * <code>{@link PathsPreferencesProvider#getPreferences(org.eclipse.core.resources.IProject)}</code>
 *
 * @author alruiz@google.com (Alex Ruiz)
 */
public class PathsPreferences {

  private final PathResolutionType pathResolutionType;
  private final List<DirectoryPath> importRoots;

  PathsPreferences(IPreferenceStore store) {
    pathResolutionType = PathResolutionType.readFrom(store);
    importRoots = importRoots(pathResolutionType, store);
  }

  private static List<DirectoryPath> importRoots(PathResolutionType types, IPreferenceStore store) {
    if (types.equals(SINGLE_DIRECTORY)) return emptyList();
    List<DirectoryPath> roots = new ArrayList<DirectoryPath>();
    for (String root : store.getString(IMPORT_ROOTS).split(CSV_PATTERN)) {
      roots.add(DirectoryPath.parse(root));
    }
    return unmodifiableList(roots);
  }

  public PathResolutionType pathResolutionType() {
    return pathResolutionType;
  }

  public List<DirectoryPath> importRoots() {
    return importRoots;
  }
}
