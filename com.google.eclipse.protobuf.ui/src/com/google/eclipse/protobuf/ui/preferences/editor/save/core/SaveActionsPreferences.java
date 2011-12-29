/*
 * Copyright (c) 2011 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.ui.preferences.editor.save.core;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.xtext.ui.editor.preferences.IPreferenceStoreAccess;

import com.google.eclipse.protobuf.ui.preferences.BooleanPreference;

/**
 * "Save actions" preferences, retrieved from an <code>{@link IPreferenceStore}</code>.
 *
 * @author alruiz@google.com (Alex Ruiz)
 */
public class SaveActionsPreferences {
  private final BooleanPreference removeTrailingWhitespace;
  private final BooleanPreference inAllLines;
  private final BooleanPreference inEditedLines;

  /**
   * Creates a new <code>{@link SaveActionsPreferences}</code>.
   * @param storeAccess simplified access to Eclipse's preferences.
   */
  public SaveActionsPreferences(IPreferenceStoreAccess storeAccess) {
    this(storeAccess.getWritablePreferenceStore());
  }

  /**
   * Creates a new <code>{@link SaveActionsPreferences}</code>.
   * @param store a table mapping named preferences to values.
   */
  public SaveActionsPreferences(IPreferenceStore store) {
    removeTrailingWhitespace = new BooleanPreference("saveActions.removeTrailingWhitespace", store);
    inAllLines = new BooleanPreference("saveActions.inAllLines", store);
    inEditedLines = new BooleanPreference("saveActions.inEditedLines", store);
  }

  /**
   * Returns the setting that specifies whether trailing whitespace should be removed.
   * @return the setting that specifies whether trailing whitespace should be removed.
   */
  public BooleanPreference removeTrailingWhitespace() {
    return removeTrailingWhitespace;
  }

  /**
   * Returns the setting that specifies whether trailing whitespace should be removed from all the lines of a file.
   * @return the setting that specifies whether trailing whitespace should be removed from all the lines of a file.
   */
  public BooleanPreference inAllLines() {
    return inAllLines;
  }

  /**
   * Returns the setting that specifies whether trailing whitespace should be removed from edited lines only.
   * @return the setting that specifies whether trailing whitespace should be removed from edited lines only.
   */
  public BooleanPreference inEditedLines() {
    return inEditedLines;
  }
}