/*
 * Copyright (c) 2011 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.ui.preferences.pages.general;

import static com.google.eclipse.protobuf.ui.preferences.binding.BindingToButtonSelection.bindSelectionOf;
import static com.google.eclipse.protobuf.ui.preferences.pages.general.EnableProjectSettingsPreference.enableProjectSettings;
import static com.google.eclipse.protobuf.ui.preferences.pages.general.Messages.*;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.*;
import org.eclipse.swt.widgets.*;

import com.google.eclipse.protobuf.ui.preferences.BooleanPreference;
import com.google.eclipse.protobuf.ui.preferences.binding.PreferenceBinder;
import com.google.eclipse.protobuf.ui.preferences.pages.PreferenceAndPropertyPage;

/**
 * @author alruiz@google.com (Alex Ruiz)
 */
public class GeneralPreferencePage extends PreferenceAndPropertyPage {

  private static final String PREFERENCE_PAGE_ID = "com.google.eclipse.protobuf.Protobuf";

  private Group grpValidation;
  private Button btnValidateOnActivation;

  @Override protected void doCreateContents(Composite parent) {
    grpValidation = new Group(parent, SWT.NONE);
    grpValidation.setLayout(new GridLayout(1, false));
    grpValidation.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1));
    grpValidation.setText(contentValidation);

    btnValidateOnActivation = new Button(grpValidation, SWT.CHECK);
    btnValidateOnActivation.setText(validateFilesOnActivation);
    new Label(parent, SWT.NONE);
  }

  @Override protected BooleanPreference enableProjectSettingsPreference(IPreferenceStore store) {
    return enableProjectSettings(store);
  }

  @Override protected void setupBinding(PreferenceBinder preferenceBinder) {
    RawPreferences preferences = new RawPreferences(getPreferenceStore());
    preferenceBinder.addAll(
        bindSelectionOf(btnValidateOnActivation).to(preferences.validateFilesOnActivation())
      );
  }

  @Override protected void updateContents() {
    if (isPropertyPage()) {
      boolean useProjectSettings = areProjectSettingsActive();
      activateProjectSettings(useProjectSettings);
      enableProjectSpecificOptions(useProjectSettings);
    }
  }

  @Override protected void onProjectSettingsActivation(boolean active) {
    enableProjectSpecificOptions(active);
  }

  private void enableProjectSpecificOptions(boolean isEnabled) {
    grpValidation.setEnabled(isEnabled);
    btnValidateOnActivation.setEnabled(isEnabled);
  }

  @Override protected String preferencePageId() {
    return PREFERENCE_PAGE_ID;
  }
}
