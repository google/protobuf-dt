/*
 * Copyright (c) 2011 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.ui.preferences.pages.compiler;

import static com.google.eclipse.protobuf.ui.preferences.EventListeners.addSelectionListener;
import static com.google.eclipse.protobuf.ui.preferences.binding.BindingToButtonSelection.bindSelectionOf;
import static com.google.eclipse.protobuf.ui.preferences.binding.BindingToTextValue.bindTextOf;
import static com.google.eclipse.protobuf.ui.preferences.pages.compiler.BindingToCodeGeneration.bindCodeGeneration;
import static com.google.eclipse.protobuf.ui.preferences.pages.compiler.EnableProjectSettingsPreference.enableProjectSettings;
import static com.google.eclipse.protobuf.ui.preferences.pages.compiler.Messages.*;
import static com.google.eclipse.protobuf.ui.swt.Colors.widgetBackground;
import static java.util.Arrays.asList;
import static org.eclipse.xtext.util.Strings.isEmpty;

import java.io.File;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.*;
import org.eclipse.swt.layout.*;
import org.eclipse.swt.widgets.*;
import org.eclipse.xtext.ui.PluginImageHelper;

import com.google.eclipse.protobuf.ui.preferences.*;
import com.google.eclipse.protobuf.ui.preferences.binding.PreferenceBinder;
import com.google.eclipse.protobuf.ui.preferences.pages.PreferenceAndPropertyPage;
import com.google.inject.Inject;

/**
 * Preference page for protobuf compiler.
 *
 * @author alruiz@google.com (Alex Ruiz)
 */
public class CompilerPreferencePage extends PreferenceAndPropertyPage {

  private static final String PREFERENCE_PAGE_ID = CompilerPreferencePage.class.getName();

  private Button btnCompileProtoFiles;
  private TabFolder tabFolder;
  private TabItem tbtmMain;
  private TabItem tbtmRefresh;
  private Group grpCompilerLocation;
  private Button btnUseProtocInSystemPath;
  private Button btnUseProtocInCustomPath;
  private Text txtProtocFilePath;
  private Button btnProtocPathBrowse;
  private Group grpCodeGeneration;
  private CodeGenerationEditor codeGenerationEditor;
  private Button btnRefreshResources;
  private Group grpRefresh;
  private Button btnRefreshProject;
  private Button btnRefreshOutputDirectory;

  @Inject private PluginImageHelper imageHelper;

  private final CodeGenerationSettings codeGenerationSettings = new CodeGenerationSettings();
  private Group grpDescriptorLocation;
  private Text txtDescriptorFilePath;
  private Button btnDescriptorPathBrowse;

  @Override protected void doCreateContents(Composite parent) {
    btnCompileProtoFiles = new Button(parent, SWT.CHECK);
    btnCompileProtoFiles.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, true, false, 2, 1));
    btnCompileProtoFiles.setText(compileOnSave);

    tabFolder = new TabFolder(parent, SWT.NONE);
    tabFolder.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1));

    tbtmMain = new TabItem(tabFolder, SWT.NONE);
    tbtmMain.setText(tabMain);

    Composite cmpMain = new Composite(tabFolder, SWT.NONE);
    tbtmMain.setControl(cmpMain);
    cmpMain.setLayout(new GridLayout(1, false));

    grpCompilerLocation = new Group(cmpMain, SWT.NONE);
    grpCompilerLocation.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
    grpCompilerLocation.setLayout(new GridLayout(2, false));
    grpCompilerLocation.setText(protocLocation);

    btnUseProtocInSystemPath = new Button(grpCompilerLocation, SWT.RADIO);
    btnUseProtocInSystemPath.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, true, false, 2, 1));
    btnUseProtocInSystemPath.setText(protocInSystemPath);

    btnUseProtocInCustomPath = new Button(grpCompilerLocation, SWT.RADIO);
    btnUseProtocInCustomPath.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, true, false, 2, 1));
    btnUseProtocInCustomPath.setText(protocInCustomPath);

    txtProtocFilePath = new Text(grpCompilerLocation, SWT.BORDER);
    txtProtocFilePath.setBackground(widgetBackground());
    txtProtocFilePath.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
    txtProtocFilePath.setEditable(false);

    btnProtocPathBrowse = new Button(grpCompilerLocation, SWT.NONE);
    btnProtocPathBrowse.setText(browseCustomPath);

    grpDescriptorLocation = new Group(cmpMain, SWT.NONE);
    grpDescriptorLocation.setText(descriptorLocation);
    grpDescriptorLocation.setLayout(new GridLayout(2, false));
    grpDescriptorLocation.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

    txtDescriptorFilePath = new Text(grpDescriptorLocation, SWT.BORDER);
    txtDescriptorFilePath.setBackground(widgetBackground());
    txtDescriptorFilePath.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
    txtDescriptorFilePath.setEditable(false);

    btnDescriptorPathBrowse = new Button(grpDescriptorLocation, SWT.NONE);
    btnDescriptorPathBrowse.setText(browseCustomPath);

    grpCodeGeneration = new Group(cmpMain, SWT.NONE);
    grpCodeGeneration.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
    grpCodeGeneration.setText(codeGeneration);
    grpCodeGeneration.setLayout(new GridLayout(1, false));

    codeGenerationEditor = new CodeGenerationEditor(grpCodeGeneration, imageHelper);
    codeGenerationEditor.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false, 1, 1));

    tbtmRefresh = new TabItem(tabFolder, SWT.NONE);
    tbtmRefresh.setText(tabRefresh);

    Composite cmpRefresh = new Composite(tabFolder, SWT.NONE);
    tbtmRefresh.setControl(cmpRefresh);
    cmpRefresh.setLayout(new GridLayout(1, false));

    btnRefreshResources = new Button(cmpRefresh, SWT.CHECK);
    btnRefreshResources.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
    btnRefreshResources.setText(refreshResources);

    grpRefresh = new Group(cmpRefresh, SWT.NONE);
    grpRefresh.setLayout(new GridLayout(1, false));
    grpRefresh.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

    btnRefreshProject = new Button(grpRefresh, SWT.RADIO);
    btnRefreshProject.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
    btnRefreshProject.setText(refreshProject);

    btnRefreshOutputDirectory = new Button(grpRefresh, SWT.RADIO);
    btnRefreshOutputDirectory.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
    btnRefreshOutputDirectory.setText(refreshOutputProject);
    new Label(parent, SWT.NONE);

    addEventListeners();
  }

  private void addEventListeners() {
    btnCompileProtoFiles.addSelectionListener(new SelectionAdapter() {
      @Override public void widgetSelected(SelectionEvent e) {
        boolean selected = btnCompileProtoFiles.getSelection();
        enableCompilerOptions(selected);
        checkState();
      }
    });
    addSelectionListener(new SelectionAdapter() {
      @Override public void widgetSelected(SelectionEvent e) {
        boolean selected = btnUseProtocInCustomPath.getSelection();
        enableCompilerCustomPathOptions(selected);
        checkState();
      }
    }, asList(btnUseProtocInCustomPath, btnUseProtocInSystemPath));
    btnProtocPathBrowse.addSelectionListener(new SelectionAdapter() {
      @Override public void widgetSelected(SelectionEvent e) {
        FileDialog dialog = new FileDialog(getShell(), SWT.OPEN | SWT.SHEET);
        String file = dialog.open();
        if (file != null) txtProtocFilePath.setText(file);
        checkState();
      }
    });
    btnDescriptorPathBrowse.addSelectionListener(new SelectionAdapter() {
      @Override public void widgetSelected(SelectionEvent e) {
        FileDialog dialog = new FileDialog(getShell(), SWT.OPEN | SWT.SHEET);
        dialog.setFilterExtensions(new String[] { "*.proto" });
        String file = dialog.open();
        if (file != null) txtDescriptorFilePath.setText(file);
        checkState();
      }
    });
    btnRefreshResources.addSelectionListener(new SelectionAdapter() {
      @Override public void widgetSelected(SelectionEvent e) {
        refreshResourcesOptionsEnabled(btnRefreshResources.getSelection());
      }
    });
    codeGenerationEditor.setDataChangedListener(new DataChangedListener() {
      public void dataChanged() {
        checkState();
      }
    });
  }

  private void checkState() {
    boolean atLeastOneLanguageEnabled = false;
    for (CodeGenerationSetting option : codeGenerationSettings.allSettings()) {
      if (option.isEnabled()) {
        atLeastOneLanguageEnabled = true;
        break;
      }
    }
    if (!atLeastOneLanguageEnabled) {
      pageIsNowInvalid(errorNoLanguageEnabled);
      return;
    }
    if (customPathOptionSelectedAndEnabled()) {
      String protocPath = txtProtocFilePath.getText();
      if (isEmpty(protocPath)) {
        pageIsNowInvalid(errorNoSelection);
        return;
      }
      File protoc = new File(protocPath);
      if (!protoc.isFile()) {
        pageIsNowInvalid(errorInvalidProtoc);
        return;
      }
    }
    String descriptorPath = txtDescriptorFilePath.getText();
    if (!isEmpty(descriptorPath) && !isFileWithName(descriptorPath, "descriptor.proto")) {
      pageIsNowInvalid(errorInvalidDescriptor);
      return;
    }
    pageIsNowValid();
  }

  private boolean isFileWithName(String filePath, String expectedFileName) {
    File file = new File(filePath);
    if (!file.isFile()) return false;
    String fileName = file.getName();
    return expectedFileName.equals(fileName);
  }

  @Override protected BooleanPreference enableProjectSettingsPreference(IPreferenceStore store) {
    return enableProjectSettings(store);
  }

  @Override protected void setupBinding(PreferenceBinder preferenceBinder) {
    RawPreferences preferences = new RawPreferences(getPreferenceStore());
    preferenceBinder.addAll(
        bindSelectionOf(btnCompileProtoFiles).to(preferences.compileProtoFiles()),
        bindSelectionOf(btnUseProtocInSystemPath).to(preferences.useProtocInSystemPath()),
        bindSelectionOf(btnUseProtocInCustomPath).to(preferences.useProtocInCustomPath()),
        bindTextOf(txtProtocFilePath).to(preferences.protocPath()),
        bindTextOf(txtDescriptorFilePath).to(preferences.descriptorPath()),
        bindSelectionOf(btnRefreshResources).to(preferences.refreshResources()),
        bindSelectionOf(btnRefreshProject).to(preferences.refreshProject()),
        bindSelectionOf(btnRefreshOutputDirectory).to(preferences.refreshOutputDirectory()),
        bindCodeGeneration(codeGenerationSettings.java())
          .to(preferences.javaCodeGenerationEnabled(), preferences.javaOutputDirectory()),
        bindCodeGeneration(codeGenerationSettings.cpp())
          .to(preferences.cppCodeGenerationEnabled(), preferences.cppOutputDirectory()),
        bindCodeGeneration(codeGenerationSettings.python())
          .to(preferences.pythonCodeGenerationEnabled(), preferences.pythonOutputDirectory())
      );
  }

  @Override protected void updateContents() {
    boolean compileProtoFiles = btnCompileProtoFiles.getSelection();
    boolean shouldEnableCompilerOptions = compileProtoFiles;
    if (isPropertyPage()) {
      boolean useProjectSettings = areProjectSettingsActive();
      activateProjectSettings(useProjectSettings);
      enableProjectSpecificOptions(useProjectSettings);
      shouldEnableCompilerOptions = shouldEnableCompilerOptions && useProjectSettings;
    }
    enableCompilerOptions(shouldEnableCompilerOptions);
    codeGenerationEditor.codeGenerationSettings(codeGenerationSettings);
  }

  @Override protected void onProjectSettingsActivation(boolean active) {
    enableProjectSpecificOptions(active);
    enableCompilerOptions(isEnabledAndSelected(btnCompileProtoFiles));
  }

  private void enableProjectSpecificOptions(boolean isEnabled) {
    btnCompileProtoFiles.setEnabled(isEnabled);
  }

  private void enableCompilerOptions(boolean isEnabled) {
    tabFolder.setEnabled(isEnabled);
    enableCompilerPathOptions(isEnabled);
    enableDescriptorPathOptions(isEnabled);
    enableOutputOptions(isEnabled);
    enableRefreshOptions(isEnabled);
  }

  private void enableCompilerPathOptions(boolean isEnabled) {
    grpCompilerLocation.setEnabled(isEnabled);
    btnUseProtocInSystemPath.setEnabled(isEnabled);
    btnUseProtocInCustomPath.setEnabled(isEnabled);
    enableCompilerCustomPathOptions(customPathOptionSelectedAndEnabled());
  }

  private void enableCompilerCustomPathOptions(boolean isEnabled) {
    txtProtocFilePath.setEnabled(isEnabled);
    btnProtocPathBrowse.setEnabled(isEnabled);
  }

  private void enableDescriptorPathOptions(boolean isEnabled) {
    grpDescriptorLocation.setEnabled(isEnabled);
    txtDescriptorFilePath.setEnabled(isEnabled);
    btnDescriptorPathBrowse.setEnabled(isEnabled);
  }

  private boolean customPathOptionSelectedAndEnabled() {
    return isEnabledAndSelected(btnUseProtocInCustomPath);
  }

  private boolean isEnabledAndSelected(Button b) {
    return b.isEnabled() && b.getSelection();
  }

  private void enableOutputOptions(boolean isEnabled) {
    grpCodeGeneration.setEnabled(isEnabled);
    codeGenerationEditor.setEnabled(isEnabled);
  }

  private void enableRefreshOptions(boolean isEnabled) {
    btnRefreshResources.setEnabled(isEnabled);
    refreshResourcesOptionsEnabled(isEnabledAndSelected(btnRefreshResources));
  }

  private void refreshResourcesOptionsEnabled(boolean isEnabled) {
    grpRefresh.setEnabled(isEnabled);
    btnRefreshProject.setEnabled(isEnabled);
    btnRefreshOutputDirectory.setEnabled(isEnabled);
  }

  @Override protected String preferencePageId() {
    return PREFERENCE_PAGE_ID;
  }
}
