/*
 * Copyright (c) 2011 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.ui.swt;

import static com.google.eclipse.protobuf.ui.swt.Messages.*;
import static org.eclipse.core.runtime.IStatus.ERROR;
import static org.eclipse.core.runtime.Status.OK_STATUS;
import static org.eclipse.jface.window.Window.OK;
import static org.eclipse.ui.views.navigator.ResourceComparator.NAME;

import org.eclipse.core.resources.*;
import org.eclipse.core.runtime.*;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.dialogs.ElementTreeSelectionDialog;
import org.eclipse.ui.dialogs.ISelectionStatusValidator;
import org.eclipse.ui.model.WorkbenchContentProvider;
import org.eclipse.ui.model.WorkbenchLabelProvider;
import org.eclipse.ui.views.navigator.ResourceComparator;

/**
 * Launches a dialog where users can browse a workspace.
 * 
 * @author alruiz@google.com (Alex Ruiz)
 */
public class BrowseWorkspaceDialogLauncher {

  private static final String PLUGIN_ID = "com.google.eclipse.protobuf.ui";

  public static String showSelectWorkspaceDirectoryDialog(Shell shell, String text, IProject project) {
    String currentPathText = text.replaceAll("\"", "");
    IPath path = new Path(currentPathText);
    ElementTreeSelectionDialog dialog = new ElementTreeSelectionDialog(shell, new WorkbenchLabelProvider(),
        new WorkbenchContentProvider());
    dialog.setInput(project == null ? workspaceRoot() : project);
    dialog.setComparator(new ResourceComparator(NAME));
    IResource container = null;
    if (path.isAbsolute()) {
      IContainer containers[] = workspaceRoot().findContainersForLocation(path);
      if (containers != null && containers.length > 0) container = containers[0];
    }
    dialog.setInitialSelection(container);
    dialog.setValidator(new ISelectionStatusValidator() {
      public IStatus validate(Object[] selection) {
        if (selection != null && selection.length > 0 && selection[0] instanceof IFile)
          return new Status(ERROR, PLUGIN_ID, errorElementIsNotDirectory);
        return OK_STATUS;
      }
    });
    dialog.setTitle(browseWorkspaceFolderTitle);
    dialog.setMessage(browseWorkspaceFolderPrompt);
    if (dialog.open() != OK) return null;
    IResource resource = (IResource) dialog.getFirstResult();
    if (resource == null) return null;
    StringBuilder b = new StringBuilder();
    return b.append("${").append("workspace_loc:").append(resource.getFullPath()).append("}").toString();
  }

  private static IWorkspaceRoot workspaceRoot() {
    return ResourcesPlugin.getWorkspace().getRoot();
  }

  private BrowseWorkspaceDialogLauncher() {}
}
