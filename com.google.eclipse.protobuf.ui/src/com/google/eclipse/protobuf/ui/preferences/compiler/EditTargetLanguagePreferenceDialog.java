/*
 * Copyright (c) 2011 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.ui.preferences.compiler;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.*;

/**
 * Dialog where users can edit language preferences.
 *
 * @author alruiz@google.com (Alex Ruiz)
 */
public class EditTargetLanguagePreferenceDialog extends Dialog {

  private final Shell parent;
  private final TargetLanguagePreference preference;

  private boolean result;
  private Shell shell;

  private Text txtOutputDirectory;
  private Button btnEnabled;
  private Button btnOk;
  private Button btnCancel;

  /**
   * Creates a new </code>{@link EditTargetLanguagePreferenceDialog}</code>.
   * @param parent a shell which will be the parent of the new instance.
   * @param preference the preference to edit.
   */
  public EditTargetLanguagePreferenceDialog(Shell parent, TargetLanguagePreference preference) {
    super(parent, SWT.NONE);
    this.parent = parent;
    this.preference = preference;
    getStyle();
    setText("Preferences for " + preference.language().name());
  }
  /**
   * Opens this dialog.
   * @return {@code true} if the user made a selection and pressed "OK" or {@code false} if the user pressed "Cancel."
   */
  public boolean open() {
    result = false;
    shell = new Shell(parent, SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL | SWT.RESIZE);
    shell.setText(getText());
    shell.setLayout(new GridLayout(3, false));

    btnEnabled = new Button(shell, SWT.CHECK);
    btnEnabled.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 3, 1));
    btnEnabled.setText("Enabled");

    Label lblOutputDirectoryName = new Label(shell, SWT.NONE);
    lblOutputDirectoryName.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
    lblOutputDirectoryName.setText("Output directory name:");

    txtOutputDirectory = new Text(shell, SWT.BORDER);
    txtOutputDirectory.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false, 1, 1));
    new Label(shell, SWT.NONE);

    Label lblError = new Label(shell, SWT.NONE);
    lblError.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1));
    new Label(shell, SWT.NONE);

    Composite composite = new Composite(shell, SWT.NONE);
    composite.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, true, false, 2, 1));
    composite.setLayout(new GridLayout(2, true));

    btnOk = new Button(composite, SWT.NONE);
    btnOk.setEnabled(false);
    btnOk.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
    btnOk.setBounds(0, 0, 92, 29);
    btnOk.setText("OK");

    btnCancel = new Button(composite, SWT.NONE);
    btnCancel.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
    btnCancel.setText("Cancel");

    shell.setDefaultButton(btnOk);
    shell.pack();

    return result;
  }
}
