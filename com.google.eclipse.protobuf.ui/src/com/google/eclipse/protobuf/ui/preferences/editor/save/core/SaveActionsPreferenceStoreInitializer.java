/*
 * Copyright (c) 2011 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.ui.preferences.editor.save.core;

import org.eclipse.xtext.ui.editor.preferences.*;

/**
 * Initializes default values for <code>{@link SaveActionsPreferences}</code>.
 *
 * @author alruiz@google.com (Alex Ruiz)
 */
public class SaveActionsPreferenceStoreInitializer implements IPreferenceStoreInitializer {
  @Override public void initialize(IPreferenceStoreAccess storeAccess) {
    SaveActionsPreferences preferences = new SaveActionsPreferences(storeAccess);
    preferences.removeTrailingWhitespace().setDefaultValue(true);
    preferences.inAllLines().setDefaultValue(false);
    preferences.inEditedLines().setDefaultValue(true);
  }
}
