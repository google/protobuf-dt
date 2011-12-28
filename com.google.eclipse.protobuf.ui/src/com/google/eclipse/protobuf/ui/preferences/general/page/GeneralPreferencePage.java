/*
 * Copyright (c) 2011 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.ui.preferences.general.page;

import static com.google.eclipse.protobuf.ui.preferences.general.core.GeneralPreferences.ENABLE_PROJECT_SETTINGS_PREFERENCE_NAME;
import static com.google.eclipse.protobuf.ui.preferences.general.page.Messages.*;
import static com.google.eclipse.protobuf.ui.preferences.pages.binding.BindingToButtonSelection.bindSelectionOf;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.*;
import org.eclipse.swt.widgets.*;

import com.google.eclipse.protobuf.ui.preferences.general.core.GeneralPreferences;
import com.google.eclipse.protobuf.ui.preferences.pages.PreferenceAndPropertyPage;
import com.google.eclipse.protobuf.ui.preferences.pages.binding.PreferenceBinder;

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

  @Override protected String enableProjectSettingsPreferenceName() {
    return ENABLE_PROJECT_SETTINGS_PREFERENCE_NAME;
  }

  @Override protected void setupBinding(PreferenceBinder preferenceBinder) {
    GeneralPreferences preferences = new GeneralPreferences(getPreferenceStore());
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

  private void enableProjectSpecificOptions(boolean enabled) {
    grpValidation.setEnabled(enabled);
    btnValidateOnActivation.setEnabled(enabled);
  }

  @Override protected String preferencePageId() {
    return PREFERENCE_PAGE_ID;
  }
}
