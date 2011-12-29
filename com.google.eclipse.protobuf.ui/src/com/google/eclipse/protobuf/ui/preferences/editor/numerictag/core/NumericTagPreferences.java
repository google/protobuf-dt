/*
 * Copyright (c) 2011 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.ui.preferences.editor.numerictag.core;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.xtext.ui.editor.preferences.IPreferenceStoreAccess;

import com.google.eclipse.protobuf.ui.preferences.StringListPreference;

/**
 * "Numeric tag" preferences, retrieved from an <code>{@link IPreferenceStore}</code>.
 *
 * @author alruiz@google.com (Alex Ruiz)
 */
public class NumericTagPreferences {
  private final StringListPreference patterns;

  /**
   * Creates a new <code>{@link NumericTagPreferences}</code>.
   * @param storeAccess simplified access to Eclipse's preferences.
   */
  public NumericTagPreferences(IPreferenceStoreAccess storeAccess) {
    this(storeAccess.getWritablePreferenceStore());
  }

  /**
   * Creates a new <code>{@link NumericTagPreferences}</code>.
   * @param store a table mapping named preferences to values.
   */
  public NumericTagPreferences(IPreferenceStore store) {
    patterns = new StringListPreference("numericTag.patterns", "\\t", store);
  }

  /**
   * Returns the setting that specifies the regular expression patterns to use to identify comments that track the next
   * field index.
   * @return the setting that specifies the regular expression patterns to use to identify comments that track the next
   * field index.
   */
  public StringListPreference patterns() {
    return patterns;
  }
}
