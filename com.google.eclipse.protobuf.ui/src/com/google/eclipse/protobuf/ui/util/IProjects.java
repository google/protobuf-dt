/*
 * Copyright (c) 2011 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.ui.util;

import static com.google.eclipse.protobuf.ui.util.Workbenches.activeWorkbenchPage;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IViewReference;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.views.navigator.ResourceNavigator;

import com.google.inject.Singleton;

/**
 * Utility methods related to <code>{@link IProject}</code>s.
 *
 * @author alruiz@google.com (Alex Ruiz)
 */
@SuppressWarnings("deprecation")
@Singleton public class IProjects {
  private static final IViewReference[] NO_VIEW_REFERENCES = new IViewReference[0];

  public IProject activeProject() {
    for (IViewReference reference : viewReferencesInActivePage()) {
      IViewPart part = reference.getView(false);
      if (part instanceof ResourceNavigator) {
        ResourceNavigator navigator = (ResourceNavigator) part;
        StructuredSelection selection = (StructuredSelection) navigator.getTreeViewer().getSelection();
        IResource resource = (IResource) selection.getFirstElement();
        return resource.getProject();
      }
    }
    return null;
  }

  private IViewReference[] viewReferencesInActivePage() {
    IWorkbenchPage page = activeWorkbenchPage();
    if (page == null) {
      return NO_VIEW_REFERENCES;
    }
    IViewReference[] references = page.getViewReferences();
    return (references == null) ? NO_VIEW_REFERENCES : references;
  }
}
