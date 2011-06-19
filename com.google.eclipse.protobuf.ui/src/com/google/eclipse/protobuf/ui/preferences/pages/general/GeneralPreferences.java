/*
 * Copyright (c) 2011 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.ui.preferences.pages.general;

import org.eclipse.core.resources.IProject;
import org.eclipse.jface.preference.IPreferenceStore;

/**
 * General preferences, retrieved from an <code>{@link IPreferenceStore}</code>. To create a new instance invoke
 * <code>{@link GeneralPreferencesFactory#preferences(IProject)}</code>
 *
 * @author alruiz@google.com (Alex Ruiz)
 */
public class GeneralPreferences {

  private final boolean validateFilesOnActivation;

  GeneralPreferences(RawPreferences preferences) {
    validateFilesOnActivation = preferences.validateFilesOnActivation().value();
  }

  public boolean validateFilesOnActivation() {
    return validateFilesOnActivation;
  }
}
