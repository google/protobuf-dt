/*
 * Copyright (c) 2011 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made
 * available under the terms of the Eclipse License v1.0 which accompanies this
 * distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.ui.preferences.pages.editor.numerictag;

import org.eclipse.jface.preference.IPreferenceStore;

import com.google.eclipse.protobuf.ui.preferences.StringListPreference;

/**
 * @author alruiz@google.com (Alex Ruiz)
 */
class RawPreferences {

  private final StringListPreference patterns;

  RawPreferences(IPreferenceStore store) {
    patterns = new StringListPreference("numericTag.patterns", "\\t", store);
  }

  StringListPreference patterns() {
    return patterns;
  }
}
