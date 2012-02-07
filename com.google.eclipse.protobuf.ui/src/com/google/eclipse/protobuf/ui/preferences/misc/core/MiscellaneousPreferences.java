/*
 * Copyright (c) 2011 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.ui.preferences.misc.core;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.xtext.ui.editor.preferences.IPreferenceStoreAccess;

import com.google.eclipse.protobuf.ui.preferences.BooleanPreference;

/**
 * "Miscellaneous" preferences, retrieved from an <code>{@link IPreferenceStore}</code>.
 *
 * @author alruiz@google.com (Alex Ruiz)
 */
public class MiscellaneousPreferences {
  private final BooleanPreference googleInternal;

  /**
   * Creates a new <code>{@link MiscellaneousPreferences}</code>.
   * @param storeAccess simplified access to Eclipse's preferences.
   */
  public MiscellaneousPreferences(IPreferenceStoreAccess storeAccess) {
    this(storeAccess.getWritablePreferenceStore());
  }

  /**
   * Creates a new <code>{@link MiscellaneousPreferences}</code>.
   * @param store a table mapping named preferences to values.
   */
  public MiscellaneousPreferences(IPreferenceStore store) {
    googleInternal = new BooleanPreference("misc.googleInternal", store);
  }

  /**
   * Returns the setting that specifies whether the editor is being used inside Google.
   * @return the setting that specifies whether the editor is being used inside Google.
   */
  public BooleanPreference isGoogleInternal() {
    return googleInternal;
  }
}
