/*
 * Copyright (c) 2011 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.ui.preferences.general;

import static com.google.eclipse.protobuf.preferences.general.PreferenceNames.DESCRIPTOR_PROTO_PATH;
import static com.google.eclipse.protobuf.preferences.general.PreferenceNames.ENABLE_PROJECT_SETTINGS_PREFERENCE_NAME;
import static com.google.eclipse.protobuf.preferences.general.PreferenceNames.VALIDATE_FILES_ON_ACTIVATION;
import static com.google.eclipse.protobuf.ui.preferences.general.Messages.contentValidation;
import static com.google.eclipse.protobuf.ui.preferences.general.Messages.errorCannotResolveOptionsDefinitionFile;
import static com.google.eclipse.protobuf.ui.preferences.general.Messages.errorEmptyOptionsDefinitionFile;
import static com.google.eclipse.protobuf.ui.preferences.general.Messages.optionsDefinitionFile;
import static com.google.eclipse.protobuf.ui.preferences.general.Messages.validateFilesOnActivation;
import static com.google.eclipse.protobuf.ui.preferences.pages.binding.BindingToButtonSelection.bindSelectionOf;
import static com.google.eclipse.protobuf.ui.preferences.pages.binding.BindingToTextValue.bindTextOf;
import static org.eclipse.xtext.util.Strings.isEmpty;

import com.google.eclipse.protobuf.preferences.general.PreferenceNames;
import com.google.eclipse.protobuf.scoping.IUriResolver;
import com.google.eclipse.protobuf.ui.preferences.pages.PreferenceAndPropertyPage;
import com.google.eclipse.protobuf.ui.preferences.pages.binding.PreferenceBinder;
import com.google.eclipse.protobuf.ui.preferences.pages.binding.PreferenceFactory;
import com.google.inject.Inject;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

/**
 * @author alruiz@google.com (Alex Ruiz)
 */
public class GeneralPreferencePage extends PreferenceAndPropertyPage {
  private static final String PREFERENCE_PAGE_ID = "com.google.eclipse.protobuf.Protobuf";

  private Group grpValidation;
  private Button btnValidateOnActivation;

  private Label lblDescriptorPath;
  private Text txtDescriptorPath;

  @Inject private IUriResolver resolver;

  @Override protected void doCreateContents(Composite parent) {
    grpValidation = new Group(parent, SWT.NONE);
    grpValidation.setLayout(new GridLayout(1, false));
    grpValidation.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1));
    grpValidation.setText(contentValidation);

    btnValidateOnActivation = new Button(grpValidation, SWT.CHECK);
    btnValidateOnActivation.setText(validateFilesOnActivation);
    new Label(parent, SWT.NONE);

    lblDescriptorPath = new Label(parent, SWT.NONE);
    lblDescriptorPath.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1));
    lblDescriptorPath.setText(optionsDefinitionFile);

    txtDescriptorPath = new Text(parent, SWT.BORDER);
    txtDescriptorPath.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

    addEventListeners();
  }

  private void addEventListeners() {
    txtDescriptorPath.addModifyListener(new ModifyListener() {
      @Override
      public void modifyText(ModifyEvent e) {
        checkState();
      }
    });
  }

  private void checkState() {
    String descriptorPathText = txtDescriptorPath.getText();
    if (isEmpty(descriptorPathText)) {
      pageIsNowInvalid(errorEmptyOptionsDefinitionFile);
      return;
    }

    if (isPropertyPage() && !canResolve(descriptorPathText)) {
      pageIsNowInvalid(errorCannotResolveOptionsDefinitionFile);
      return;
    }
    pageIsNowValid();
  }

  // TODO(het): Resolve based on unsaved preferences, rather than saved preferences
  private boolean canResolve(String descriptorPathText) {
    if (PreferenceNames.DEFAULT_DESCRIPTOR_PATH.equals(descriptorPathText)) {
      return true;
    }
    return resolver.resolveUri(descriptorPathText, null, project()) != null;
  }

  @Override protected String enableProjectSettingsPreferenceName() {
    return ENABLE_PROJECT_SETTINGS_PREFERENCE_NAME;
  }

  @Override protected void setupBinding(PreferenceBinder binder, PreferenceFactory factory) {
    binder.addAll(
        bindSelectionOf(btnValidateOnActivation).to(factory.newBooleanPreference(VALIDATE_FILES_ON_ACTIVATION)),
        bindTextOf(txtDescriptorPath).to(factory.newStringPreference(DESCRIPTOR_PROTO_PATH))
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
    txtDescriptorPath.setEnabled(enabled);
  }

  @Override protected String preferencePageId() {
    return PREFERENCE_PAGE_ID;
  }
}
