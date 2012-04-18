/*
 * Copyright (c) 2011 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.ui.wizards;

import static com.google.common.io.Closeables.closeQuietly;
import static com.google.eclipse.protobuf.ui.util.IStatusFactory.error;
import static com.google.eclipse.protobuf.ui.util.Workbenches.activeWorkbenchPage;
import static com.google.eclipse.protobuf.ui.util.Workspaces.workspaceRoot;
import static org.eclipse.jface.dialogs.MessageDialog.openError;
import static org.eclipse.ui.ide.IDE.openEditor;

import java.io.*;
import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.resources.*;
import org.eclipse.core.runtime.*;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.*;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.*;

/**
 * Wizard for creation of new .proto files.
 *
 * @author alruiz@google.com (Alex Ruiz)
 */
public class NewProtoFileWizard extends Wizard implements INewWizard {
  private NewProtoFileWizardPage page;
  private ISelection selection;

  public NewProtoFileWizard() {
    setNeedsProgressMonitor(true);
  }

  @Override public void addPages() {
    page = new NewProtoFileWizardPage(selection);
    addPage(page);
  }

  @Override public boolean performFinish() {
    try {
      getContainer().run(true, false, new IRunnableWithProgress() {
        @Override public void run(IProgressMonitor monitor) throws InvocationTargetException {
          try {
            doFinish(page.containerName(), page.fileName(), monitor);
          } catch (CoreException e) {
            throw new InvocationTargetException(e);
          } finally {
            monitor.done();
          }
        }
      });
    } catch (InterruptedException e) {
      return false;
    } catch (InvocationTargetException e) {
      Throwable realException = e.getTargetException();
      openError(getShell(), "Error", realException.getMessage());
      return false;
    }
    return true;
  }

  private void doFinish(String containerName, String fileName, IProgressMonitor monitor) throws CoreException {
    monitor.beginTask("Creating " + fileName, 2);
    IResource resource = workspaceRoot().findMember(new Path(containerName));
    if (!resource.exists() || !(resource instanceof IContainer)) {
      throw new CoreException(error("Container \"" + containerName + "\" does not exist."));
    }
    IContainer container = (IContainer) resource;
    final IFile file = container.getFile(new Path(fileName));
    InputStream stream = null;
    try {
      stream = openContentStream();
      if (file.exists()) {
        file.setContents(stream, true, true, monitor);
      } else {
        file.create(stream, true, monitor);
      }
    } finally {
      closeQuietly(stream);
    }
    monitor.worked(1);
    monitor.setTaskName("Opening file for editing...");
    getShell().getDisplay().asyncExec(new Runnable() {
      @Override public void run() {
        IWorkbenchPage activePage = activeWorkbenchPage();
        try {
          openEditor(activePage, file, true);
        } catch (PartInitException e) {
        }
      }
    });
    monitor.worked(1);
  }

  private InputStream openContentStream() {
    String contents = "syntax = \"proto2\";";
    return new ByteArrayInputStream(contents.getBytes());
  }

  @Override public void init(IWorkbench workbench, IStructuredSelection newSelection) {
    selection = newSelection;
  }
}