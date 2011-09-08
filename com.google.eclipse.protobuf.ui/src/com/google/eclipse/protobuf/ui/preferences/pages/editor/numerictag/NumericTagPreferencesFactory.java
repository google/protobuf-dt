/*
 * Copyright (c) 2011 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.ui.preferences.pages.editor.numerictag;

import com.google.inject.Inject;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.xtext.ui.editor.preferences.IPreferenceStoreAccess;

/**
 * Factory of <code>{@link NumericTagPreferences}</code>.
 *
 * @author alruiz@google.com (Alex Ruiz)
 */
public class NumericTagPreferencesFactory {

  @Inject private IPreferenceStoreAccess storeAccess;

  public NumericTagPreferences preferences() {
    IPreferenceStore store = storeAccess.getWritablePreferenceStore();
    return new NumericTagPreferences(new RawPreferences(store));
  }
}
