/*
 * Copyright (c) 2011 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.ui.wizards;

import static com.google.eclipse.protobuf.ui.util.Workspaces.workspaceRoot;
import static org.eclipse.jface.window.Window.OK;
import static org.eclipse.swt.SWT.*;
import static org.eclipse.swt.layout.GridData.FILL_HORIZONTAL;

import org.eclipse.core.resources.*;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.viewers.*;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.events.*;
import org.eclipse.swt.layout.*;
import org.eclipse.swt.widgets.*;
import org.eclipse.ui.dialogs.ContainerSelectionDialog;

/**
 * @author alruiz@google.com (Alex Ruiz)
 */
public class NewProtoFileWizardPage extends WizardPage {
  private Text containerText;
  private Text fileText;
  private final ISelection selection;

  public NewProtoFileWizardPage(ISelection selection) {
    super("wizardPage");
    setTitle("Multi-page Editor File");
    setDescription("This wizard creates a new file with *.proto extension that can be opened by a multi-page editor.");
    this.selection = selection;
  }

  @Override public void createControl(Composite parent) {
    Composite container = new Composite(parent, NULL);
    GridLayout layout = new GridLayout();
    container.setLayout(layout);
    layout.numColumns = 3;
    layout.verticalSpacing = 9;
    Label label = new Label(container, NULL);
    label.setText("&Container:");

    containerText = new Text(container, BORDER | SINGLE);
    containerText.setLayoutData(new GridData(FILL_HORIZONTAL));
    containerText.addModifyListener(new ModifyListener() {
      @Override public void modifyText(ModifyEvent e) {
        dialogChanged();
      }
    });

    Button button = new Button(container, PUSH);
    button.setText("Browse...");
    button.addSelectionListener(new SelectionAdapter() {
      @Override public void widgetSelected(SelectionEvent e) {
        handleBrowse();
      }
    });
    label = new Label(container, NULL);
    label.setText("&File name:");

    fileText = new Text(container, BORDER | SINGLE);
    fileText.setLayoutData(new GridData(FILL_HORIZONTAL));
    fileText.addModifyListener(new ModifyListener() {
      @Override public void modifyText(ModifyEvent e) {
        dialogChanged();
      }
    });
    initialize();
    dialogChanged();
    setControl(container);
  }

  private void initialize() {
    Object selected = firstSelectedElement();
    if (selected != null) {
      if (selected instanceof IResource) {
        IContainer container;
        if (selected instanceof IContainer) {
          container = (IContainer) selected;
        } else {
          container = ((IResource) selected).getParent();
        }
        containerText.setText(container.getFullPath().toString());
      }
    }
    fileText.setText("new_file.proto");
  }

  private Object firstSelectedElement() {
    if (selection == null || selection.isEmpty()) {
      return null;
    }
    if (selection instanceof IStructuredSelection) {
      ((IStructuredSelection) selection).getFirstElement();
    }
    return null;
  }

  private void handleBrowse() {
    ContainerSelectionDialog dialog = new ContainerSelectionDialog(getShell(), workspaceRoot(), false,
        "Select new file container");
    if (dialog.open() == OK) {
      Object[] result = dialog.getResult();
      if (result.length == 1) {
        containerText.setText(((Path) result[0]).toString());
      }
    }
  }

  private void dialogChanged() {
    IResource container = ResourcesPlugin.getWorkspace().getRoot().findMember(new Path(containerName()));
    String fileName = fileName();
    if (containerName().isEmpty()) {
      updateStatus("File container must be specified");
      return;
    }
    if (container == null || (container.getType() & (IResource.PROJECT | IResource.FOLDER)) == 0) {
      updateStatus("File container must exist");
      return;
    }
    if (!container.isAccessible()) {
      updateStatus("Project must be writable");
      return;
    }
    if (fileName.isEmpty()) {
      updateStatus("File name must be specified");
      return;
    }
    if (fileName.replace('\\', '/').indexOf('/', 1) > 0) {
      updateStatus("File name must be valid");
      return;
    }
    int dotLoc = fileName.lastIndexOf('.');
    if (dotLoc != -1) {
      String ext = fileName.substring(dotLoc + 1);
      if (ext.equalsIgnoreCase("proto") == false) {
        updateStatus("File extension must be \"proto\"");
        return;
      }
    }
    updateStatus(null);
  }

  private void updateStatus(String message) {
    setErrorMessage(message);
    setPageComplete(message == null);
  }

  public String containerName() {
    return containerText.getText();
  }

  public String fileName() {
    return fileText.getText();
  }
}