/*
 * Copyright (c) 2011 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.ui.preferences;

import static com.google.eclipse.protobuf.ui.preferences.CompilerPreferenceNames.REFRESH_PROJECT;

import org.eclipse.jface.preference.IPreferenceStore;

/**
 * The type of resource to refresh after calling protoc.
 *
 * @author alruiz@google.com (Alex Ruiz)
 */
public enum RefreshTarget {

  PROJECT, OUTPUT_FOLDER;

  static RefreshTarget find(IPreferenceStore store) {
    if (store.getBoolean(REFRESH_PROJECT)) return PROJECT;
    return OUTPUT_FOLDER;
  }
}
