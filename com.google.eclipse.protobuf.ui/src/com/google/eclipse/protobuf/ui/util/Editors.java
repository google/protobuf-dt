/*
 * Copyright (c) 2012 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.ui.util;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.ui.IEditorPart;

import com.google.inject.Singleton;

/**
 * Utility methods related to editors.
 *
 * @author alruiz@google.com (Alex Ruiz)
 */
@Singleton public class Editors {

  /**
   * Returns the project owning the file displayed in the given editor.
   * @param editor the given editor.
   * @return the project owning the file displayed in the given editor.
   */
  public IProject projectOwningFileDisplayedIn(IEditorPart editor) {
    IResource resource = resourceFrom(editor);
    return (resource == null) ? null : resource.getProject();
  }

  private IResource resourceFrom(IEditorPart editor) {
    if (editor == null) {
      return null;
    }
    Object adapter = editor.getEditorInput().getAdapter(IResource.class);
    return (adapter == null) ? null : (IResource) adapter;
  }
}
