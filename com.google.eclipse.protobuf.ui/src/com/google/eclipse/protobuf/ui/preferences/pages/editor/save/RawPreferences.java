/*
 * Copyright (c) 2011 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made
 * available under the terms of the Eclipse License v1.0 which accompanies this
 * distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.ui.preferences.pages.editor.save;

import org.eclipse.jface.preference.IPreferenceStore;

import com.google.eclipse.protobuf.ui.preferences.BooleanPreference;

/**
 * @author alruiz@google.com (Alex Ruiz)
 */
class RawPreferences {

  private final BooleanPreference removeTrailingWhitespace;
  private final BooleanPreference inAllLines;
  private final BooleanPreference inEditedLines;

  RawPreferences(IPreferenceStore store) {
    removeTrailingWhitespace = new BooleanPreference("saveActions.removeTrailingWhitespace", store);
    inAllLines = new BooleanPreference("saveActions.inAllLines", store);
    inEditedLines = new BooleanPreference("saveActions.inEditedLines", store);
  }

  BooleanPreference removeTrailingWhitespace() {
    return removeTrailingWhitespace;
  }

  BooleanPreference inAllLines() {
    return inAllLines;
  }

  BooleanPreference inEditedLines() {
    return inEditedLines;
  }
}
