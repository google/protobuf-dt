/*
 * Copyright (c) 2011 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.ui.preferences.paths;

import static com.google.eclipse.protobuf.ui.preferences.paths.PathsPreferenceNames.DIRECTORY_NAMES;
import static com.google.eclipse.protobuf.ui.preferences.paths.PathResolutionType.SINGLE_DIRECTORY;
import static com.google.eclipse.protobuf.ui.util.Strings.CSV_PATTERN;
import static java.util.Arrays.asList;
import static java.util.Collections.*;

import java.util.List;

import org.eclipse.jface.preference.IPreferenceStore;

/**
 * Paths preferences, retrieved from an <code>{@link IPreferenceStore}</code>.
 *
 * @author alruiz@google.com (Alex Ruiz)
 */
public class PathsPreferences {

  private final PathResolutionType pathResolutionType;
  private final List<String> directoryNames;

  PathsPreferences(IPreferenceStore store) {
    pathResolutionType = PathResolutionType.readFrom(store);
    directoryNames = directoryNames(pathResolutionType, store);
  }

  private static List<String> directoryNames(PathResolutionType types, IPreferenceStore store) {
    if (types.equals(SINGLE_DIRECTORY)) return emptyList();
    String[] directoryNames = store.getString(DIRECTORY_NAMES).split(CSV_PATTERN);
    return unmodifiableList(asList(directoryNames));
  }

  public PathResolutionType pathResolutionType() {
    return pathResolutionType;
  }

  public List<String> directoryNames() {
    return directoryNames;
  }
}
