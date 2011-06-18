/*
 * Copyright (c) 2011 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */package com.google.eclipse.protobuf.ui.preferences.pages.paths;

import static com.google.eclipse.protobuf.ui.preferences.pages.paths.PreferenceNames.FILES_IN_ONE_DIRECTORY_ONLY;

import org.eclipse.jface.preference.IPreferenceStore;

/**
 * Types of file resolution.
 * 
 * @author alruiz@google.com (Alex Ruiz)
 */
public enum PathResolutionType {

  SINGLE_DIRECTORY, MULTIPLE_DIRECTORIES;
  
  static PathResolutionType readFrom(IPreferenceStore store) {
    if (store.getBoolean(FILES_IN_ONE_DIRECTORY_ONLY)) return SINGLE_DIRECTORY;
    return MULTIPLE_DIRECTORIES;
  }
}
