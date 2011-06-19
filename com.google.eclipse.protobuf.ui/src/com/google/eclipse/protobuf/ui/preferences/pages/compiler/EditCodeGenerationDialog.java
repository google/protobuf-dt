/*
 * Copyright (c) 2011 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.ui.preferences.pages.compiler;

import static com.google.eclipse.protobuf.ui.preferences.pages.compiler.Messages.*;
import static org.eclipse.core.resources.IResource.FOLDER;
import static org.eclipse.jface.dialogs.IDialogConstants.OK_ID;
import static org.eclipse.swt.layout.GridData.*;
import static org.eclipse.xtext.util.Strings.isEmpty;

import org.eclipse.core.resources.*;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.*;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.*;

import com.google.eclipse.protobuf.ui.preferences.InputDialog;

/**
 * Dialog where users can edit a single code generation option.
 *
 * @author alruiz@google.com (Alex Ruiz)
 */
public class EditCodeGenerationDialog extends InputDialog {

  private final CodeGenerationSetting option;

  private Text txtOutputDirectory;
  private Button btnEnabled;
  private Text txtError;

  /**
   * Creates a new </code>{@link EditCodeGenerationDialog}</code>.
   * @param parent a shell which will be the parent of the new instance.
   * @param option the code generation option to edit.
   */
  public EditCodeGenerationDialog(Shell parent, CodeGenerationSetting option) {
    super(parent, editCodeGenerationOptionTitle + option.language().name());
    this.option = option;
  }

  /** {@inheritDoc} */
  @Override protected Control createDialogArea(Composite parent) {
    Composite cmpDialogArea = (Composite) super.createDialogArea(parent);

    btnEnabled = new Button(cmpDialogArea, SWT.CHECK);
    btnEnabled.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 3, 1));
    btnEnabled.setText(enabled);
    btnEnabled.setSelection(option.isEnabled());

    Label lblOutputDirectoryName = new Label(cmpDialogArea, SWT.NONE);
    lblOutputDirectoryName.setText(outputDirectoryPrompt);

    txtOutputDirectory = new Text(cmpDialogArea, SWT.BORDER);
    txtOutputDirectory.setEnabled(option.isEnabled());

    GridData gd_txtOutputDirectory = new GridData(SWT.FILL, SWT.TOP, true, false, 1, 1);
    gd_txtOutputDirectory.widthHint = 200;
    txtOutputDirectory.setLayoutData(gd_txtOutputDirectory);
    txtOutputDirectory.setText(option.outputDirectory());

    txtError = new Text(cmpDialogArea, SWT.READ_ONLY | SWT.WRAP);
    GridData gd_lblError = new GridData(GRAB_HORIZONTAL | HORIZONTAL_ALIGN_FILL);
    gd_lblError.horizontalSpan = 2;
    txtError.setLayoutData(gd_lblError);
    txtError.setBackground(txtError.getDisplay().getSystemColor(SWT.COLOR_WIDGET_BACKGROUND));

    addEventListeners();

    applyDialogFont(cmpDialogArea);
    return cmpDialogArea;
  }

  private void addEventListeners() {
    btnEnabled.addSelectionListener(new SelectionAdapter() {
      @Override public void widgetSelected(SelectionEvent e) {
        txtOutputDirectory.setEnabled(btnEnabled.getSelection());
        checkState();
      }
    });
    txtOutputDirectory.addModifyListener(new ModifyListener() {
      public void modifyText(ModifyEvent e) {
        checkState();
      }
    });
  }

  private void checkState() {
    if (btnEnabled.getSelection()) {
      if (!outputDirectoryEntered()) {
        pageIsNowInvalid(errorEnterDirectoryName);
        return;
      }
      String errorMessage = validateDirectoryName(enteredOuptutDirectory());
      if (errorMessage != null) {
        pageIsNowInvalid(errorMessage);
        return;
      }
    }
    pageIsNowValid();
  }

  private String validateDirectoryName(String directoryName) {
    IWorkspace workspace = ResourcesPlugin.getWorkspace();
    IStatus isValid = workspace.validateName(directoryName, FOLDER);
    return (isValid.getCode() == OK) ? null : isValid.getMessage();
  }

  private void pageIsNowInvalid(String errorMessage) {
    txtError.setText(errorMessage);
    getButton(OK_ID).setEnabled(false);
  }

  private void pageIsNowValid() {
    txtError.setText("");
    getButton(OK_ID).setEnabled(true);
  }

  /** {@inheritDoc} */
  @Override protected void okPressed() {
    option.enabled(btnEnabled.getSelection());
    option.outputDirectory(enteredOuptutDirectory());
    super.okPressed();
  }

  private boolean outputDirectoryEntered() {
    return !isEmpty(enteredOuptutDirectory());
  }

  private String enteredOuptutDirectory() {
    return txtOutputDirectory.getText().trim();
  }
}
