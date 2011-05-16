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
import static com.google.eclipse.protobuf.ui.preferences.paths.PathsPreferenceNames.DIRECTORY_PATHS;
import static com.google.eclipse.protobuf.ui.util.Strings.CSV_PATTERN;
import static java.util.Collections.*;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.preference.IPreferenceStore;

/**
 * Paths preferences, retrieved from an <code>{@link IPreferenceStore}</code>.
 *
 * @author alruiz@google.com (Alex Ruiz)
 */
public class PathsPreferences {

  private final PathResolutionType pathResolutionType;
  private final List<DirectoryPath> directoryPaths;

  PathsPreferences(IPreferenceStore store) {
    pathResolutionType = PathResolutionType.readFrom(store);
    directoryPaths = directoryPaths(pathResolutionType, store);
  }

  private static List<DirectoryPath> directoryPaths(PathResolutionType types, IPreferenceStore store) {
    if (types.equals(SINGLE_DIRECTORY)) return emptyList();
    List<DirectoryPath> paths = new ArrayList<DirectoryPath>();
    for (String directoryPath : store.getString(DIRECTORY_PATHS).split(CSV_PATTERN)) {
      paths.add(DirectoryPath.parse(directoryPath));
    }
    return unmodifiableList(paths);
  }

  public PathResolutionType pathResolutionType() {
    return pathResolutionType;
  }

  public List<DirectoryPath> directoryPaths() {
    return directoryPaths;
  }
}
