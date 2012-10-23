/*
 * Copyright (c) 2011 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.ui.wizards;

import static com.google.eclipse.protobuf.ui.util.Workbenches.activeWorkbenchPage;
import static com.google.eclipse.protobuf.ui.wizards.Messages.wizardTitle;

import org.eclipse.core.resources.IFile;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.ide.IDE;

/**
 * Wizard for creation of new .proto files.
 *
 * @author alruiz@google.com (Alex Ruiz)
 */
public class NewProtoFileWizard extends Wizard implements INewWizard {
  private NewProtoFileWizardPage page;
  private IStructuredSelection selection;

  public NewProtoFileWizard() {
    setWindowTitle(wizardTitle);
  }

  @Override public void addPages() {
    page = new NewProtoFileWizardPage(selection);
    addPage(page);
  }

  @Override public boolean performFinish() {
    final IFile file = page.createNewFile();
    if (file == null) {
      return false;
    }
    getShell().getDisplay().asyncExec(new Runnable() {
      @Override public void run() {
        try {
          IDE.openEditor(activeWorkbenchPage(), file, true);
        } catch (PartInitException ignored) {}
      }
    });
    return true;
  }

  @Override public void init(IWorkbench workbench, IStructuredSelection newSelection) {
    selection = newSelection;
  }
}