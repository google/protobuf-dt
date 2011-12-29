/*
 * Copyright (c) 2011 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.ui.preferences.paths.page;

import static com.google.eclipse.protobuf.ui.ProtobufUiModule.PLUGIN_ID;
import static com.google.eclipse.protobuf.ui.preferences.paths.page.Messages.*;
import static org.eclipse.core.runtime.IStatus.ERROR;
import static org.eclipse.core.runtime.Status.OK_STATUS;
import static org.eclipse.jface.window.Window.OK;
import static org.eclipse.ui.views.navigator.ResourceComparator.NAME;

import java.net.URI;

import org.eclipse.core.resources.*;
import org.eclipse.core.runtime.*;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.*;
import org.eclipse.ui.dialogs.*;
import org.eclipse.ui.model.*;
import org.eclipse.ui.views.navigator.ResourceComparator;

/**
 * Launchers for dialogs where users can select a directory (either in a workspace or the file system.)
 *
 * @author alruiz@google.com (Alex Ruiz)
 */
class DirectorySelectionDialogs {
  static IPath showWorkspaceDirectorySelectionDialog(Shell shell, String initialPath) {
    return showWorkspaceDirectorySelectionDialog(shell, initialPath, null);
  }

  static IPath showWorkspaceDirectorySelectionDialog(Shell shell, String initialPath, IProject project) {
    String currentPathText = initialPath.replaceAll("\"", "");
    URI uri = URI.create(currentPathText);
    ElementTreeSelectionDialog dialog = new ElementTreeSelectionDialog(shell, new WorkbenchLabelProvider(),
        new WorkbenchContentProvider());
    IWorkspaceRoot workspaceRoot = workspaceRoot();
    dialog.setInput(project == null ? workspaceRoot : project);
    dialog.setComparator(new ResourceComparator(NAME));
    IResource container = null;
    if (uri.isAbsolute()) {
      IContainer containers[] = workspaceRoot.findContainersForLocationURI(uri);
      if (containers != null && containers.length > 0) {
        container = containers[0];
      }
    }
    dialog.setInitialSelection(container);
    dialog.setValidator(new ISelectionStatusValidator() {
      @Override public IStatus validate(Object[] selection) {
        if (selection != null && selection.length > 0 && selection[0] instanceof IFile) {
          return new Status(ERROR, PLUGIN_ID, errorElementIsNotDirectory);
        }
        return OK_STATUS;
      }
    });
    dialog.setTitle(directorySelection);
    dialog.setMessage(selectWorkspaceDirectory);
    if (dialog.open() != OK) {
      return null;
    }
    IResource resource = (IResource) dialog.getFirstResult();
    return (resource == null) ? null : resource.getFullPath();
  }

  static String showFileSystemDirectorySelectionDialog(Shell shell, String filterPath) {
    DirectoryDialog dialog = new DirectoryDialog(shell, SWT.OPEN | SWT.APPLICATION_MODAL);
    if (filterPath != null && filterPath.trim().length() != 0) {
      dialog.setFilterPath(filterPath);
    }
    dialog.setMessage(selectFileSystemDirectory);
    return dialog.open();
  }

  private static IWorkspaceRoot workspaceRoot() {
    return ResourcesPlugin.getWorkspace().getRoot();
  }

  private DirectorySelectionDialogs() {}
}
