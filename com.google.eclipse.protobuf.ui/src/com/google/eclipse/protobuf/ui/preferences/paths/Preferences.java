/*
 * Copyright (c) 2011 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.ui.preferences.paths;

import static com.google.eclipse.protobuf.ui.preferences.paths.FileResolutionType.SINGLE_FOLDER;
import static com.google.eclipse.protobuf.ui.preferences.paths.PreferenceNames.*;
import static com.google.eclipse.protobuf.ui.util.Strings.CSV_PATTERN;
import static java.util.Arrays.asList;
import static java.util.Collections.*;

import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.xtext.ui.editor.preferences.IPreferenceStoreAccess;

/**
 * Paths preferences, retrieved from an <code>{@link IPreferenceStore}</code>.
 * 
 * @author alruiz@google.com (Alex Ruiz)
 */
public class Preferences {

  public final FileResolutionType fileResolutionType;
  public final List<String> folderNames; 
  
  public static Preferences loadPreferences(IPreferenceStoreAccess access, IProject project) {
    IPreferenceStore store = access.getWritablePreferenceStore(project);
    boolean useProjectPreferences = store.getBoolean(ENABLE_PROJECT_SETTINGS);
    if (!useProjectPreferences) store = access.getWritablePreferenceStore();
    return new Preferences(store);
  }
  
  private Preferences(IPreferenceStore store) {
    fileResolutionType = FileResolutionType.find(store);
    folderNames = folderNames(fileResolutionType, store);
  }
  
  private static List<String> folderNames(FileResolutionType types, IPreferenceStore store) {
    if (types.equals(SINGLE_FOLDER)) return emptyList();
    String[] folderNames = store.getString(FOLDER_NAMES).split(CSV_PATTERN);
    return unmodifiableList(asList(folderNames));
  }
}
