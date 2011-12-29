/*
 * Copyright (c) 2011 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.ui.preferences.compiler.page;

import static com.google.eclipse.protobuf.ui.preferences.compiler.core.CompilerPreferences.ENABLE_PROJECT_SETTINGS_PREFERENCE_NAME;
import static com.google.eclipse.protobuf.ui.preferences.compiler.page.Messages.*;
import static com.google.eclipse.protobuf.ui.preferences.pages.ButtonGroup.with;
import static com.google.eclipse.protobuf.ui.preferences.pages.LabelWidgets.setEnabled;
import static com.google.eclipse.protobuf.ui.preferences.pages.TextWidgets.*;
import static com.google.eclipse.protobuf.ui.preferences.pages.binding.BindingToButtonSelection.bindSelectionOf;
import static com.google.eclipse.protobuf.ui.preferences.pages.binding.BindingToTextValue.bindTextOf;
import static org.eclipse.xtext.util.Strings.isEmpty;

import java.io.File;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.*;
import org.eclipse.swt.layout.*;
import org.eclipse.swt.widgets.*;

import com.google.eclipse.protobuf.ui.preferences.compiler.core.CompilerPreferences;
import com.google.eclipse.protobuf.ui.preferences.pages.PreferenceAndPropertyPage;
import com.google.eclipse.protobuf.ui.preferences.pages.binding.PreferenceBinder;

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
  private TabItem tbtmOptions;
  private Group grpCompilerLocation;
  private Button btnUseProtocInSystemPath;
  private Button btnUseProtocInCustomPath;
  private Text txtProtocFilePath;
  private Button btnProtocPathBrowse;
  private Group grpDescriptorLocation;
  private Text txtDescriptorFilePath;
  private Button btnDescriptorPathBrowse;
  private Button btnGenerateJava;
  private Label lblJavaOutputDirectory;
  private Text txtJavaOutputDirectory;
  private Button btnGenerateCpp;
  private Label lblCppOutputDirectory;
  private Text txtCppOutputDirectory;
  private Button btnGeneratePython;
  private Label lblPythonOutputDirectory;
  private Text txtPythonOutputDirectory;
  private Group grpRefresh;
  private Button btnRefreshResources;
  private Button btnRefreshProject;
  private Button btnRefreshOutputDirectory;

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
    txtProtocFilePath.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
    setEditable(txtProtocFilePath, false);

    btnProtocPathBrowse = new Button(grpCompilerLocation, SWT.NONE);
    btnProtocPathBrowse.setText(browseCustomPath);

    grpDescriptorLocation = new Group(cmpMain, SWT.NONE);
    grpDescriptorLocation.setText(descriptorLocation);
    grpDescriptorLocation.setLayout(new GridLayout(2, false));
    grpDescriptorLocation.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

    txtDescriptorFilePath = new Text(grpDescriptorLocation, SWT.BORDER);
    txtDescriptorFilePath.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
    setEditable(txtDescriptorFilePath, false);

    btnDescriptorPathBrowse = new Button(grpDescriptorLocation, SWT.NONE);
    btnDescriptorPathBrowse.setText(browseCustomPath);

    tbtmOptions = new TabItem(tabFolder, SWT.NONE);
    tbtmOptions.setText("&Options");

    Composite cmpOptions = new Composite(tabFolder, SWT.NONE);
    tbtmOptions.setControl(cmpOptions);
    cmpOptions.setLayout(new GridLayout(2, false));

    btnGenerateJava = new Button(cmpOptions, SWT.CHECK);
    btnGenerateJava.setEnabled(false);
    btnGenerateJava.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, true, false, 3, 1));
    btnGenerateJava.setText("Generate Java");

    lblJavaOutputDirectory = new Label(cmpOptions, SWT.NONE);
    lblJavaOutputDirectory.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
    lblJavaOutputDirectory.setText("Java Output Directory:");

    txtJavaOutputDirectory = new Text(cmpOptions, SWT.BORDER);
    txtJavaOutputDirectory.setEnabled(false);
    txtJavaOutputDirectory.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

    btnGenerateCpp = new Button(cmpOptions, SWT.CHECK);
    btnGenerateCpp.setEnabled(false);
    btnGenerateCpp.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, true, false, 2, 1));
    btnGenerateCpp.setText("Generate C++");

    lblCppOutputDirectory = new Label(cmpOptions, SWT.NONE);
    lblCppOutputDirectory.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
    lblCppOutputDirectory.setText("C++ Output Directory:");

    txtCppOutputDirectory = new Text(cmpOptions, SWT.BORDER);
    txtCppOutputDirectory.setEnabled(false);
    txtCppOutputDirectory.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false, 1, 1));

    btnGeneratePython = new Button(cmpOptions, SWT.CHECK);
    btnGeneratePython.setEnabled(false);
    btnGeneratePython.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, true, false, 2, 1));
    btnGeneratePython.setText("Generate Python");

    lblPythonOutputDirectory = new Label(cmpOptions, SWT.NONE);
    lblPythonOutputDirectory.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
    lblPythonOutputDirectory.setText("Python Output Directory:");

    txtPythonOutputDirectory = new Text(cmpOptions, SWT.BORDER);
    txtPythonOutputDirectory.setEnabled(false);
    txtPythonOutputDirectory.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false, 1, 1));

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
        enableCompilerSettings(selected);
        checkState();
      }
    });
    with(btnUseProtocInCustomPath, btnUseProtocInSystemPath).add(new SelectionAdapter() {
      @Override public void widgetSelected(SelectionEvent e) {
        boolean selected = btnUseProtocInCustomPath.getSelection();
        enableCompilerCustomPathSettings(selected);
        checkState();
      }
    });
    btnProtocPathBrowse.addSelectionListener(new SelectionAdapter() {
      @Override public void widgetSelected(SelectionEvent e) {
        FileDialog dialog = new FileDialog(getShell(), SWT.OPEN | SWT.SHEET);
        String file = dialog.open();
        if (file != null) {
          txtProtocFilePath.setText(file);
        }
        checkState();
      }
    });
    btnDescriptorPathBrowse.addSelectionListener(new SelectionAdapter() {
      @Override public void widgetSelected(SelectionEvent e) {
        FileDialog dialog = new FileDialog(getShell(), SWT.OPEN | SWT.SHEET);
        dialog.setFilterExtensions(new String[] { "*.proto" });
        String file = dialog.open();
        if (file != null) {
          txtDescriptorFilePath.setText(file);
        }
        checkState();
      }
    });
    btnGenerateJava.addSelectionListener(new SelectionAdapter() {
      @Override public void widgetSelected(SelectionEvent e) {
        enableJavaOutputDirectory(btnGenerateJava.getSelection());
        checkState();
      }
    });
    btnGenerateCpp.addSelectionListener(new SelectionAdapter() {
      @Override public void widgetSelected(SelectionEvent e) {
        enableCppOutputDirectory(btnGenerateCpp.getSelection());
        checkState();
      }
    });
    btnGeneratePython.addSelectionListener(new SelectionAdapter() {
      @Override public void widgetSelected(SelectionEvent e) {
        enablePythonOutputDirectory(btnGeneratePython.getSelection());
        checkState();
      }
    });
    btnRefreshResources.addSelectionListener(new SelectionAdapter() {
      @Override public void widgetSelected(SelectionEvent e) {
        refreshResourcesSettingsEnabled(btnRefreshResources.getSelection());
      }
    });
  }

  @Override protected String enableProjectSettingsPreferenceName() {
    return ENABLE_PROJECT_SETTINGS_PREFERENCE_NAME;
  }

  @Override protected void setupBinding(PreferenceBinder preferenceBinder) {
    CompilerPreferences preferences = new CompilerPreferences(getPreferenceStore());
    preferenceBinder.addAll(
        bindSelectionOf(btnCompileProtoFiles).to(preferences.compileProtoFiles()),
        bindSelectionOf(btnUseProtocInSystemPath).to(preferences.useProtocInSystemPath()),
        bindSelectionOf(btnUseProtocInCustomPath).to(preferences.useProtocInCustomPath()),
        bindTextOf(txtProtocFilePath).to(preferences.protocPath()),
        bindTextOf(txtDescriptorFilePath).to(preferences.descriptorPath()),
        bindSelectionOf(btnGenerateJava).to(preferences.javaCodeGenerationEnabled()),
        bindTextOf(txtJavaOutputDirectory).to(preferences.javaOutputDirectory()),
        bindSelectionOf(btnGenerateCpp).to(preferences.cppCodeGenerationEnabled()),
        bindTextOf(txtCppOutputDirectory).to(preferences.cppOutputDirectory()),
        bindSelectionOf(btnGeneratePython).to(preferences.pythonCodeGenerationEnabled()),
        bindTextOf(txtPythonOutputDirectory).to(preferences.pythonOutputDirectory()),
        bindSelectionOf(btnRefreshResources).to(preferences.refreshResources()),
        bindSelectionOf(btnRefreshProject).to(preferences.refreshProject()),
        bindSelectionOf(btnRefreshOutputDirectory).to(preferences.refreshOutputDirectory())
      );
  }

  @Override protected void updateContents() {
    boolean compileProtoFiles = btnCompileProtoFiles.getSelection();
    boolean shouldEnableCompilerOptions = compileProtoFiles;
    if (isPropertyPage()) {
      boolean useProjectSettings = areProjectSettingsActive();
      activateProjectSettings(useProjectSettings);
      enableProjectSpecificSettings(useProjectSettings);
      shouldEnableCompilerOptions = shouldEnableCompilerOptions && useProjectSettings;
    }
    enableCompilerSettings(shouldEnableCompilerOptions);
  }

  @Override protected void onProjectSettingsActivation(boolean active) {
    enableProjectSpecificSettings(active);
    enableCompilerSettings(isEnabledAndSelected(btnCompileProtoFiles));
    checkState();
  }

  private void enableProjectSpecificSettings(boolean enabled) {
    btnCompileProtoFiles.setEnabled(enabled);
  }

  private void enableCompilerSettings(boolean enabled) {
    enableCompilerPathSettings(enabled);
    enableDescriptorPathSettings(enabled);
    enableOptionsSettings(enabled);
    enableRefreshSettings(enabled);
  }

  private void enableCompilerPathSettings(boolean enabled) {
    grpCompilerLocation.setEnabled(enabled);
    btnUseProtocInSystemPath.setEnabled(enabled);
    btnUseProtocInCustomPath.setEnabled(enabled);
    enableCompilerCustomPathSettings(customPathOptionSelectedAndEnabled());
  }

  private void enableCompilerCustomPathSettings(boolean enabled) {
    txtProtocFilePath.setEnabled(enabled);
    btnProtocPathBrowse.setEnabled(enabled);
  }

  private void enableDescriptorPathSettings(boolean enabled) {
    grpDescriptorLocation.setEnabled(enabled);
    txtDescriptorFilePath.setEnabled(enabled);
    btnDescriptorPathBrowse.setEnabled(enabled);
  }

  private boolean customPathOptionSelectedAndEnabled() {
    return isEnabledAndSelected(btnUseProtocInCustomPath);
  }

  private void enableOptionsSettings(boolean enabled) {
    btnGenerateJava.setEnabled(enabled);
    enableJavaOutputDirectory(isEnabledAndSelected(btnGenerateJava));
    btnGenerateCpp.setEnabled(enabled);
    enableCppOutputDirectory(isEnabledAndSelected(btnGenerateCpp));
    btnGeneratePython.setEnabled(enabled);
    enablePythonOutputDirectory(isEnabledAndSelected(btnGeneratePython));
  }

  private void enableJavaOutputDirectory(boolean enabled) {
    setEnabled(txtJavaOutputDirectory, enabled);
    setEnabled(lblJavaOutputDirectory, enabled);
  }

  private void enableCppOutputDirectory(boolean enabled) {
    setEnabled(txtCppOutputDirectory, enabled);
    setEnabled(lblCppOutputDirectory, enabled);
  }

  private void enablePythonOutputDirectory(boolean enabled) {
    setEnabled(txtPythonOutputDirectory, enabled);
    setEnabled(lblPythonOutputDirectory, enabled);
  }

  private void enableRefreshSettings(boolean enabled) {
    btnRefreshResources.setEnabled(enabled);
    refreshResourcesSettingsEnabled(isEnabledAndSelected(btnRefreshResources));
  }

  private boolean isEnabledAndSelected(Button button) {
    return button.isEnabled() && button.getSelection();
  }

  private void refreshResourcesSettingsEnabled(boolean enabled) {
    grpRefresh.setEnabled(enabled);
    btnRefreshProject.setEnabled(enabled);
    btnRefreshOutputDirectory.setEnabled(enabled);
  }

  private void checkState() {
    if (isPropertyPage() && !areProjectSettingsActive()) {
      // the page is a 'project property' page and the 'enable project settings' check-box is not selected
      pageIsNowValid();
      return;
    }
    if (!btnCompileProtoFiles.getSelection()) {
      // all the options of this page are disabled
      pageIsNowValid();
      return;
    }
    if (!atLeastOneTargetLanguageIsSelected()) {
      pageIsNowInvalid(errorNoLanguageSelected);
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

  private boolean atLeastOneTargetLanguageIsSelected() {
    return btnGenerateJava.getSelection() || btnGenerateCpp.getSelection() || btnGeneratePython.getSelection();
  }

  private boolean isFileWithName(String filePath, String expectedFileName) {
    File file = new File(filePath);
    if (!file.isFile()) {
      return false;
    }
    String fileName = file.getName();
    return expectedFileName.equals(fileName);
  }
  @Override protected String preferencePageId() {
    return PREFERENCE_PAGE_ID;
  }
}
