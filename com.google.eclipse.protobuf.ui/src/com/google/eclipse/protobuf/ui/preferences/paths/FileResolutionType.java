/*
 * Copyright (c) 2011 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */package com.google.eclipse.protobuf.ui.preferences.paths;

import static com.google.eclipse.protobuf.ui.preferences.paths.PreferenceNames.ALL_PROTOS_IN_ONE_FOLDER_ONLY;

import org.eclipse.jface.preference.IPreferenceStore;

/**
 * Types of file resolution.
 * 
 * @author alruiz@google.com (Alex Ruiz)
 */
public enum FileResolutionType {

  SINGLE_FOLDER, MULTIPLE_FOLDERS;
  
  static FileResolutionType find(IPreferenceStore store) {
    if (store.getBoolean(ALL_PROTOS_IN_ONE_FOLDER_ONLY)) return SINGLE_FOLDER;
    return MULTIPLE_FOLDERS;
  }
}
