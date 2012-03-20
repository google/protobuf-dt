/*
 * Copyright (c) 2011 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.ui.preferences.editor.save;

import static com.google.eclipse.protobuf.ui.preferences.editor.save.PreferenceNames.*;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.xtext.ui.editor.preferences.*;

/**
 * @author alruiz@google.com (Alex Ruiz)
 */
public class SaveActionsPreferences {
  private final IPreferenceStore store;

  public SaveActionsPreferences(IPreferenceStoreAccess storeAccess) {
    this.store = storeAccess.getWritablePreferenceStore();
  }

  public boolean shouldRemoveTrailingWhitespace() {
    return store.getBoolean(REMOVE_TRAILING_WHITESPACE);
  }

  public boolean shouldRemoveTrailingWhitespaceInAllLines() {
    return store.getBoolean(IN_ALL_LINES);
  }

  public static class Initializer implements IPreferenceStoreInitializer {
    @Override public void initialize(IPreferenceStoreAccess storeAccess) {
      IPreferenceStore store = storeAccess.getWritablePreferenceStore();
      store.setDefault(REMOVE_TRAILING_WHITESPACE, true);
      store.setDefault(IN_ALL_LINES, false);
      store.setDefault(IN_EDITED_LINES, true);
    }
  }
}