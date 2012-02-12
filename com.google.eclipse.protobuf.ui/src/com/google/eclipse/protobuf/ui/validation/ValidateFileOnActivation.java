/*
 * Copyright (c) 2011 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License v1.0 which
 * accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.ui.validation;

import static com.google.eclipse.protobuf.ui.preferences.general.core.GeneralPreferences.generalPreferences;
import static com.google.eclipse.protobuf.ui.validation.ProtobufValidation.validate;

import org.eclipse.core.resources.IProject;
import org.eclipse.ui.*;
import org.eclipse.xtext.ui.editor.preferences.IPreferenceStoreAccess;

import com.google.eclipse.protobuf.ui.preferences.general.core.GeneralPreferences;
import com.google.eclipse.protobuf.ui.util.*;

/**
 * Validates a .proto file when it is opened or activated.
 *
 * @author alruiz@google.com (Alex Ruiz)
 */
public class ValidateFileOnActivation extends AbstractPartListener {
  /**
   * Validates the active active editor in the given part that contains a .proto file in the Workspace.
   * @param ref the part that was activated.
   */
  @Override public void partActivated(IWorkbenchPartReference ref) {
    IEditorPart activeEditor = activeEditor(ref);
    IProject project = projectFrom(activeEditor);
    if (project == null || !shouldValidateEditor(project)) {
      return;
    }
    validate(activeEditor);
  }

  private IEditorPart activeEditor(IWorkbenchPartReference ref) {
    IWorkbenchPage page = ref.getPage();
    return (page == null) ? null : page.getActiveEditor();
  }

  private IProject projectFrom(IEditorPart editor) {
    Editors editors = ProtobufEditorPlugIn.getInstanceOf(Editors.class);
    return editors.projectOwningFileDisplayedIn(editor);
  }

  private boolean shouldValidateEditor(IProject project) {
    IPreferenceStoreAccess storeAccess = ProtobufEditorPlugIn.getInstanceOf(IPreferenceStoreAccess.class);
    GeneralPreferences preferences = generalPreferences(storeAccess, project);
    return preferences.validateFilesOnActivation().getValue();
  }
}
