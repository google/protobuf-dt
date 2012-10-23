/*
 * Copyright (c) 2012 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.ui.preferences.editor.save;

import static com.google.eclipse.protobuf.ui.preferences.editor.save.PreferenceNames.IN_ALL_LINES;
import static com.google.eclipse.protobuf.ui.preferences.editor.save.PreferenceNames.IN_EDITED_LINES;
import static com.google.eclipse.protobuf.ui.preferences.editor.save.PreferenceNames.REMOVE_TRAILING_WHITESPACE;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.xtext.ui.editor.preferences.IPreferenceStoreAccess;

import com.google.inject.Inject;

/**
 * @author alruiz@google.com (Alex Ruiz)
 */
public class SaveActionsWritablePreferences {
  private final IPreferenceStore store;

  @Inject public SaveActionsWritablePreferences(IPreferenceStoreAccess storeAccess) {
    store = storeAccess.getWritablePreferenceStore();
  }

  public void removeTrailingWhitespace(RemoveTrailingWhitespace value) {
    if (value == RemoveTrailingWhitespace.NONE) {
      store.setValue(REMOVE_TRAILING_WHITESPACE, false);
      return;
    }
    store.setValue(REMOVE_TRAILING_WHITESPACE, true);
    boolean inEditedLines = (value == RemoveTrailingWhitespace.EDITED_LINES);
    store.setValue(IN_ALL_LINES, !inEditedLines);
    store.setValue(IN_EDITED_LINES, inEditedLines);
  }

  public static enum RemoveTrailingWhitespace {
    NONE, EDITED_LINES, ALL_LINES;
  }
}
