/*
 * Copyright (c) 2011 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.ui.preferences.compiler;

import static com.google.eclipse.protobuf.ui.preferences.compiler.Messages.*;
import static com.google.eclipse.protobuf.ui.swt.Shells.centerShell;
import static org.eclipse.core.resources.IResource.FOLDER;
import static org.eclipse.core.runtime.IStatus.OK;
import static org.eclipse.xtext.util.Strings.isEmpty;

import org.eclipse.core.resources.*;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.*;
import org.eclipse.swt.layout.*;
import org.eclipse.swt.widgets.*;

/**
 * Dialog where users can edit a single code generation option.
 *
 * @author alruiz@google.com (Alex Ruiz)
 */
public class EditCodeGenerationOptionDialog extends Dialog {

  private final Shell parent;
  private final CodeGenerationOption option;

  private boolean result;
  private Shell shell;

  private Text txtOutputDirectory;
  private Button btnEnabled;
  private Label lblError;
  private Button btnOk;
  private Button btnCancel;

  /**
   * Creates a new </code>{@link EditCodeGenerationOptionDialog}</code>.
   * @param parent a shell which will be the parent of the new instance.
   * @param option the code generation option to edit.
   */
  public EditCodeGenerationOptionDialog(Shell parent, CodeGenerationOption option) {
    super(parent, SWT.NONE);
    this.parent = parent;
    this.option = option;
    getStyle();
    setText(editCodeGenerationOptionTitle + option.language().name());
  }
  /**
   * Opens this dialog.
   * @return {@code true} if the user made a selection and pressed "OK" or {@code false} if the user pressed "Cancel."
   */
  public boolean open() {
    result = false;
    shell = new Shell(parent, SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL | SWT.RESIZE);
    shell.setText(getText());
    createAndCenterContent();
    shell.open();
    Display display = parent.getDisplay();
    while (!shell.isDisposed())
      if (!display.readAndDispatch()) display.sleep();
    return result;
  }

  private void createAndCenterContent() {
    shell = new Shell(parent, SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL | SWT.RESIZE);
    shell.setSize(564, 158);
    shell.setText(getText());
    shell.setLayout(new GridLayout(2, true));

    btnEnabled = new Button(shell, SWT.CHECK);
    btnEnabled.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 3, 1));
    btnEnabled.setText(enabled);
    btnEnabled.setSelection(option.isEnabled());

    Label lblOutputDirectoryName = new Label(shell, SWT.NONE);
    lblOutputDirectoryName.setText(outputDirectoryPrompt);

    txtOutputDirectory = new Text(shell, SWT.BORDER);
    txtOutputDirectory.setEnabled(option.isEnabled());
    txtOutputDirectory.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false, 1, 1));
    txtOutputDirectory.setText(option.outputDirectory());

    lblError = new Label(shell, SWT.NONE);
    lblError.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1));

    Composite composite = new Composite(shell, SWT.NONE);
    composite.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, true, false, 2, 1));
    composite.setLayout(new GridLayout(2, true));

    btnOk = new Button(composite, SWT.NONE);
    btnOk.setEnabled(false);
    btnOk.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
    btnOk.setBounds(0, 0, 92, 29);
    btnOk.setText(ok);

    btnCancel = new Button(composite, SWT.NONE);
    btnCancel.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
    btnCancel.setText(cancel);

    addEventListeners();

    shell.setDefaultButton(btnOk);
    shell.pack();

    centerWindow();
  }

  private void addEventListeners() {
    btnEnabled.addSelectionListener(new SelectionAdapter() {
      @Override public void widgetSelected(SelectionEvent e) {
        txtOutputDirectory.setEnabled(btnEnabled.getSelection());
        checkState();
      }
    });
    btnOk.addSelectionListener(new SelectionAdapter() {
      @Override public void widgetSelected(SelectionEvent e) {
        option.enabled(btnEnabled.getSelection());
        option.outputDirectory(enteredOuptutDirectory());
        result = true;
        shell.dispose();
      }
    });
    btnCancel.addSelectionListener(new SelectionAdapter() {
      @Override public void widgetSelected(SelectionEvent e) {
        shell.dispose();
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
    lblError.setText(errorMessage);
    btnOk.setEnabled(false);
  }

  private void pageIsNowValid() {
    lblError.setText("");
    btnOk.setEnabled(true);
  }

  private boolean outputDirectoryEntered() {
    return !isEmpty(enteredOuptutDirectory());
  }

  private String enteredOuptutDirectory() {
    return txtOutputDirectory.getText().trim();
  }

  private void centerWindow() {
    centerShell(shell, parent);
  }
}
