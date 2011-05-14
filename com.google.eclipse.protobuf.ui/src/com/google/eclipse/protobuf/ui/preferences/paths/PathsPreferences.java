/*
 * Copyright (c) 2011 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.ui.preferences.paths;

import static com.google.eclipse.protobuf.ui.preferences.paths.PathsResolutionType.SINGLE_DIRECTORY;
import static com.google.eclipse.protobuf.ui.preferences.paths.PathsPreferenceNames.DIRECTORY_NAMES;
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

  private final PathsResolutionType fileResolutionType;
  private final List<String> folderNames; 
  
  PathsPreferences(IPreferenceStore store) {
    fileResolutionType = PathsResolutionType.find(store);
    folderNames = folderNames(fileResolutionType, store);
  }
  
  private static List<String> folderNames(PathsResolutionType types, IPreferenceStore store) {
    if (types.equals(SINGLE_DIRECTORY)) return emptyList();
    String[] folderNames = store.getString(DIRECTORY_NAMES).split(CSV_PATTERN);
    return unmodifiableList(asList(folderNames));
  }

  public PathsResolutionType fileResolutionType() {
    return fileResolutionType;
  }

  public List<String> folderNames() {
    return folderNames;
  }
}
