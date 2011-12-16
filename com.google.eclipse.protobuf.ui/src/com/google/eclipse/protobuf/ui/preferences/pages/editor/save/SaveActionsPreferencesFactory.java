/*
 * Copyright (c) 2011 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.ui.preferences.pages.editor.save;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.xtext.ui.editor.preferences.IPreferenceStoreAccess;

import com.google.inject.Inject;

/**
 * Factory of <code>{@link SaveActionsPreferences}</code>.
 *
 * @author alruiz@google.com (Alex Ruiz)
 */
public class SaveActionsPreferencesFactory {

  @Inject private IPreferenceStoreAccess storeAccess;

  public SaveActionsPreferences preferences() {
    IPreferenceStore store = storeAccess.getWritablePreferenceStore();
    return new SaveActionsPreferences(new RawPreferences(store));
  }
}
