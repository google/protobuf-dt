/*
 * Copyright (c) 2011 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.ui.builder.nature;

import com.google.inject.Inject;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.xtext.builder.nature.ToggleXtextNatureAction;
import org.eclipse.xtext.builder.nature.XtextNature;
import org.eclipse.xtext.ui.editor.IXtextEditorCallback;
import org.eclipse.xtext.ui.editor.XtextEditor;


/**
 * Automatically adds {@link XtextNature} to a project if needed (e.g. when opening a 'Protocol
 * Buffer' editor for the first time.)
 *
 * @author alruiz@google.com (Alex Ruiz)
 */
@SuppressWarnings("restriction")
public class AutoAddNatureEditorCallback extends IXtextEditorCallback.NullImpl {
  @Inject private ToggleXtextNatureAction xtext;

  @Override public void afterCreatePartControl(XtextEditor editor) {
    IResource resource = editor.getResource();
    if (resource == null) {
      return;
    }
    IProject project = resource.getProject();
    if (!project.isAccessible() || project.isHidden()) {
      return;
    }
    if (!xtext.hasNature(project)) {
      xtext.toggleNature(project);
    }
  }
}