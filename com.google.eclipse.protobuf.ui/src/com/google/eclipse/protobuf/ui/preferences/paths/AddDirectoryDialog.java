/*
 * Copyright (c) 2011 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.ui.preferences.paths;

import static org.eclipse.jface.dialogs.IDialogConstants.OK_ID;
import static org.eclipse.xtext.util.Strings.isEmpty;

import static com.google.eclipse.protobuf.ui.preferences.pages.TextWidgets.setEditable;
import static com.google.eclipse.protobuf.ui.preferences.paths.DirectorySelectionDialogs.showFileSystemDirectorySelectionDialog;
import static com.google.eclipse.protobuf.ui.preferences.paths.DirectorySelectionDialogs.showWorkspaceDirectorySelectionDialog;
import static com.google.eclipse.protobuf.ui.preferences.paths.Messages.addDirectoryPath;
import static com.google.eclipse.protobuf.ui.preferences.paths.Messages.browseFileSystem;
import static com.google.eclipse.protobuf.ui.preferences.paths.Messages.browseWorkspace;
import static com.google.eclipse.protobuf.ui.preferences.paths.Messages.directory;
import static com.google.eclipse.protobuf.ui.preferences.paths.Messages.isWorkspacePathCheck;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IPath;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import com.google.eclipse.protobuf.ui.preferences.pages.InputDialog;

/**
 * Dialog where users can select a path (in the workspace or file system) to be included in resolution of imports.
 *
 * @author alruiz@google.com (Alex Ruiz)
 */
public class AddDirectoryDialog extends InputDialog {
  private static final String DIRECTORY_PATH_FORMAT = "${workspace_loc:%s}";

  private final IProject project;

  private String selectedPath;

  private Text txtPath;
  private Button btnWorkspace;
  private Button btnIsWorkspacePath;
  private Button btnFileSystem;

  /**
   * Creates a new </code>{@link AddDirectoryDialog}</code>.
   * @param parent a shell which will be the parent of the new instance.
   * @param project the project whose properties the user is modifying.
   */
  public AddDirectoryDialog(Shell parent, IProject project) {
    super(parent, addDirectoryPath);
    this.project = project;
  }

  @Override protected Control createDialogArea(Composite parent) {
    Composite cmpDialogArea = (Composite) super.createDialogArea(parent);

    GridLayout gridLayout = (GridLayout) cmpDialogArea.getLayout();
    gridLayout.numColumns = 2;

    Label label = new Label(cmpDialogArea, SWT.NONE);
    label.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1));
    label.setText(directory);

    txtPath = new Text(cmpDialogArea, SWT.BORDER);
    txtPath.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1));
    setEditable(txtPath, false);

    Composite cmpCheckBox = new Composite(cmpDialogArea, SWT.NONE);
    cmpCheckBox.setEnabled(false);
    cmpCheckBox.setLayoutData(new GridData(SWT.RIGHT, SWT.TOP, false, false, 1, 1));
    cmpCheckBox.setLayout(new GridLayout(1, false));

    btnIsWorkspacePath = new Button(cmpCheckBox, SWT.CHECK);
    btnIsWorkspacePath.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, false, false, 1, 1));
    btnIsWorkspacePath.setSize(158, 24);
    btnIsWorkspacePath.setText(isWorkspacePathCheck);

    Composite cmpButtons = new Composite(cmpDialogArea, SWT.NONE);
    cmpButtons.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
    cmpButtons.setLayout(new GridLayout(2, true));
    new Label(cmpButtons, SWT.NONE);

    btnWorkspace = new Button(cmpButtons, SWT.NONE);
    btnWorkspace.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
    btnWorkspace.setText(browseWorkspace);
    new Label(cmpButtons, SWT.NONE);

    btnFileSystem = new Button(cmpButtons, SWT.NONE);
    btnFileSystem.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
    btnFileSystem.setText(browseFileSystem);

    addEventListeners();

    applyDialogFont(cmpDialogArea);
    return cmpDialogArea;
  }

  private void addEventListeners() {
    btnWorkspace.addSelectionListener(new SelectionAdapter() {
      @Override public void widgetSelected(SelectionEvent e) {
        IPath path = showWorkspaceDirectorySelectionDialog(getShell(), enteredPathText());
        if (path != null) {
          path = ProjectVariable.replaceProjectNameWithProjectVariable(path, project);
          txtPath.setText(path.toString().trim());
          btnIsWorkspacePath.setSelection(true);
        }
      }
    });
    btnFileSystem.addSelectionListener(new SelectionAdapter() {
      @Override public void widgetSelected(SelectionEvent e) {
        String path = showFileSystemDirectorySelectionDialog(getShell(), enteredPathText());
        if (path != null) {
          txtPath.setText(path.trim());
          btnIsWorkspacePath.setSelection(false);
        }
      }
    });
    txtPath.addModifyListener(new ModifyListener() {
      @Override public void modifyText(ModifyEvent e) {
        boolean hasText = !isEmpty(enteredPathText());
        getButton(OK_ID).setEnabled(hasText);
      }
    });
  }

  @Override protected void createButtonsForButtonBar(Composite parent) {
    super.createButtonsForButtonBar(parent);
    getButton(OK_ID).setEnabled(false);
    txtPath.setFocus();
  }

  @Override protected void okPressed() {
    selectedPath = enteredPathText();
    if (btnIsWorkspacePath.getSelection()) {
      selectedPath = String.format(DIRECTORY_PATH_FORMAT, selectedPath);
    }
    super.okPressed();
  }

  private String enteredPathText() {
    return txtPath.getText().trim();
  }

  /**
   * Returns the path selected by the user.
   * @return the path selected by the user.
   */
  public String selectedPath() {
    return selectedPath;
  }
}
