/*
 * Copyright (c) 2011 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.ui.preferences.paths;

import static com.google.eclipse.protobuf.ui.preferences.paths.Messages.*;
import static com.google.eclipse.protobuf.ui.swt.SelectDirectoryDialogLauncher.*;
import static com.google.eclipse.protobuf.ui.swt.Shells.centerShell;
import static org.eclipse.xtext.util.Strings.isEmpty;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.*;
import org.eclipse.swt.layout.*;
import org.eclipse.swt.widgets.*;

/**
 * Dialog where users can select a path (in the workspace or file system) to be included in resolution of imports.
 *
 * @author alruiz@google.com (Alex Ruiz)
 */
public class AddDirectoryDialog extends Dialog {

  private final Shell parent;

  private Shell shell;
  private boolean result;

  private DirectoryPath selectedPath;

  private Text txtPath;
  private Button btnWorkspace;
  private Button btnIsWorkspacePath;
  private Button btnFileSystem;
  private Button btnCancel;
  private Button btnOk;

  /**
   * Creates a new </code>{@link AddDirectoryDialog}</code>.
   * @param parent a shell which will be the parent of the new instance.
   * @param title the title of this dialog.
   */
  public AddDirectoryDialog(Shell parent, String title) {
    super(parent, SWT.NONE);
    this.parent = parent;
    getStyle();
    setText(title);
  }

  /**
   * Opens this dialog.
   * @return {@code true} if the user made a selection and pressed "OK" or {@code false} if the user pressed "Cancel."
   */
  public boolean open() {
    result = false;
    shell = new Shell(parent, SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL);
    shell.setSize(503, 254);
    shell.setText(getText());
    createAndCenterContent();
    shell.open();
    Display display = parent.getDisplay();
    while (!shell.isDisposed())
      if (!display.readAndDispatch()) display.sleep();
    return result;
  }

  private void createAndCenterContent() {
    shell.setLayout(new GridLayout(2, false));

    Label label = new Label(shell, SWT.NONE);
    label.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1));
    label.setText(includeDirectoryPrompt);

    txtPath = new Text(shell, SWT.BORDER);
    txtPath.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1));
    txtPath.setEditable(false);

    Composite cmpCheckBox = new Composite(shell, SWT.NONE);
    cmpCheckBox.setEnabled(false);
    cmpCheckBox.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, false, false, 1, 1));
    cmpCheckBox.setLayout(new GridLayout(1, false));

    btnIsWorkspacePath = new Button(cmpCheckBox, SWT.CHECK);
    btnIsWorkspacePath.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, false, false, 1, 1));
    btnIsWorkspacePath.setSize(158, 24);
    btnIsWorkspacePath.setText(isWorkspacePathCheck);

    Composite cmpButtons = new Composite(shell, SWT.NONE);
    cmpButtons.setLayout(new GridLayout(2, true));
    new Label(cmpButtons, SWT.NONE);

    btnWorkspace = new Button(cmpButtons, SWT.NONE);
    btnWorkspace.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
    btnWorkspace.setText(browseWorkspace);
    new Label(cmpButtons, SWT.NONE);

        btnFileSystem = new Button(cmpButtons, SWT.NONE);
        btnFileSystem.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
        btnFileSystem.setText(browseFileSystem);

    btnOk = new Button(cmpButtons, SWT.NONE);
    btnOk.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
    btnOk.setEnabled(false);
    btnOk.setText(ok);

    btnCancel = new Button(cmpButtons, SWT.NONE);
    btnCancel.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
    btnCancel.setText(cancel);

    addEventListeners();

    shell.setDefaultButton(btnOk);
    shell.pack();

    centerWindow();
  }

  private void addEventListeners() {
    btnWorkspace.addSelectionListener(new SelectionAdapter() {
      @Override public void widgetSelected(SelectionEvent e) {
        String path = showWorkspaceDirectoryDialog(shell, txtPath.getText(), null);
        if (path != null) {
          txtPath.setText(path.trim());
          btnIsWorkspacePath.setSelection(true);
        }
      }
    });
    btnFileSystem.addSelectionListener(new SelectionAdapter() {
      @Override public void widgetSelected(SelectionEvent e) {
        String path = showFileSystemFolderDialog(shell, txtPath.getText());
        if (path != null) {
          txtPath.setText(path.trim());
          btnIsWorkspacePath.setSelection(false);
        }
      }
    });
    btnOk.addSelectionListener(new SelectionAdapter() {
      @Override public void widgetSelected(SelectionEvent e) {
        selectedPath = new DirectoryPath(txtPath.getText().trim(), btnIsWorkspacePath.getSelection());
        result = true;
        shell.dispose();
      }
    });
    btnCancel.addSelectionListener(new SelectionAdapter() {
      @Override public void widgetSelected(SelectionEvent e) {
        shell.dispose();
      }
    });
    txtPath.addModifyListener(new ModifyListener() {
      public void modifyText(ModifyEvent e) {
        boolean hasText = !isEmpty(txtPath.getText().trim());
        btnOk.setEnabled(hasText);
      }
    });
  }

  private void centerWindow() {
    centerShell(shell, parent);
  }

  /**
   * Returns the path selected by the user.
   * @return the path selected by the user.
   */
  public DirectoryPath selectedPath() {
    return selectedPath;
  }
}
