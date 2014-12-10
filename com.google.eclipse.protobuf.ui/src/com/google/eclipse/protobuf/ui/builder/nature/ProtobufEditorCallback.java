/*
 * Copyright (c) 2014 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.ui.builder.nature;

import com.google.eclipse.protobuf.preferences.general.GeneralPreferences;
import com.google.eclipse.protobuf.ui.validation.ProtobufValidation;
import com.google.inject.Inject;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.xtext.builder.nature.ToggleXtextNatureAction;
import org.eclipse.xtext.builder.nature.XtextNature;
import org.eclipse.xtext.ui.editor.IXtextEditorCallback;
import org.eclipse.xtext.ui.editor.XtextEditor;
import org.eclipse.xtext.ui.editor.preferences.IPreferenceStoreAccess;


/**
 * Automatically adds {@link XtextNature} to a project if needed (e.g. when opening a 'Protocol
 * Buffer' editor for the first time) and performs validation on a protobuf file when it is opened.
 *
 * @author alruiz@google.com (Alex Ruiz)
 */
@SuppressWarnings("restriction")
public class ProtobufEditorCallback extends IXtextEditorCallback.NullImpl {
  @Inject private ToggleXtextNatureAction xtext;
  @Inject private IPreferenceStoreAccess storeAccess;
  @Inject private ProtobufValidation validator;

  @Override public void afterCreatePartControl(XtextEditor editor) {
    IResource resource = editor.getResource();
    if (resource == null) {
      return;
    }
    IProject project = resource.getProject();
    addXtextNatureToProject(project);
    validateEditorIfEnabled(editor, project);
  }

  private void addXtextNatureToProject(IProject project) {
    if (!project.isAccessible() || project.isHidden()) {
      return;
    }
    if (!xtext.hasNature(project)) {
      xtext.toggleNature(project);
    }
  }

  private void validateEditorIfEnabled(XtextEditor editor, IProject project) {
    GeneralPreferences preferences = new GeneralPreferences(storeAccess, project);
    boolean shouldValidate = preferences.shouldValidateFilesOnActivation();
    if (shouldValidate) {
      validator.validate(editor);
    }
  }
}