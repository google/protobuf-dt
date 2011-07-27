/*
 * Copyright (c) 2011 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.ui.preferences.pages.paths;

import static com.google.eclipse.protobuf.ui.preferences.pages.paths.PathResolutionType.*;
import static com.google.eclipse.protobuf.ui.util.Strings.CSV_PATTERN;
import static java.util.Collections.*;

import java.util.*;

import org.eclipse.core.resources.IProject;
import org.eclipse.jface.preference.IPreferenceStore;

/**
 * Paths preferences, retrieved from an <code>{@link IPreferenceStore}</code>.
 *
 * @author alruiz@google.com (Alex Ruiz)
 */
public class PathsPreferences {

  private final PathResolutionType pathResolutionType;
  private final List<DirectoryPath> importRoots;

  PathsPreferences(RawPreferences preferences, IProject project) {
    boolean filesInOneDirectoryOnly = preferences.filesInOneDirectoryOnly().value();
    pathResolutionType = filesInOneDirectoryOnly ? SINGLE_DIRECTORY : MULTIPLE_DIRECTORIES;
    importRoots = importRoots(preferences, project);
  }

  private List<DirectoryPath> importRoots(RawPreferences preferences, IProject project) {
    if (pathResolutionType.equals(SINGLE_DIRECTORY)) return emptyList();
    List<DirectoryPath> roots = new ArrayList<DirectoryPath>();
    for (String root : preferences.directoryPaths().value().split(CSV_PATTERN)) {
      roots.add(DirectoryPath.parse(root, project));
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
