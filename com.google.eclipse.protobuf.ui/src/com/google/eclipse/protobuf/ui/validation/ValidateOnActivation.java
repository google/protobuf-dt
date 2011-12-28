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

import static com.google.eclipse.protobuf.ui.Internals.injector;
import static com.google.eclipse.protobuf.ui.preferences.general.core.GeneralPreferences.generalPreferences;
import static com.google.eclipse.protobuf.ui.validation.Validation.validate;

import org.eclipse.core.resources.IProject;
import org.eclipse.ui.*;
import org.eclipse.xtext.ui.editor.preferences.IPreferenceStoreAccess;

import com.google.eclipse.protobuf.ui.preferences.general.core.GeneralPreferences;
import com.google.eclipse.protobuf.ui.util.Resources;

/**
 * Validates a .proto file when it is opened or activated.
 *
 * @author alruiz@google.com (Alex Ruiz)
 */
public class ValidateOnActivation implements IPartListener2 {

  /**
   * Validates the active active editor in the given part that contains a .proto file in the Workspace.
   * @param partRef the part that was activated.
   */
  @Override public void partActivated(IWorkbenchPartReference partRef) {
    IEditorPart activeEditor = activeEditor(partRef);
    IProject project = projectFrom(activeEditor);
    if (project == null || !shouldValidateEditor(project)) {
      return;
    }
    validate(activeEditor);
  }

  private IEditorPart activeEditor(IWorkbenchPartReference partRef) {
    IWorkbenchPage page = partRef.getPage();
    return (page == null) ? null : page.getActiveEditor();
  }

  private IProject projectFrom(IEditorPart editor) {
    Resources resources = injector().getInstance(Resources.class);
    return resources.project(editor);
  }

  private boolean shouldValidateEditor(IProject project) {
    IPreferenceStoreAccess storeAccess = injector().getInstance(IPreferenceStoreAccess.class);
    GeneralPreferences preferences = generalPreferences(storeAccess, project);
    return preferences.validateFilesOnActivation().getValue();
  }

  /**
   * This method does nothing.
   * @param partRef the part that was surfaced.
   */
  @Override public void partBroughtToTop(IWorkbenchPartReference partRef) {}

  /**
   * This method does nothing.
   * @param partRef the part that was closed.
   */
  @Override public void partClosed(IWorkbenchPartReference partRef) {}

  /**
   * This method does nothing.
   * @param partRef the part that was deactivated.
   */
  @Override public void partDeactivated(IWorkbenchPartReference partRef) {}

  /**
   * This method does nothing.
   * @param partRef the part that was opened.
   */
  @Override public void partOpened(IWorkbenchPartReference partRef) {}

  /**
   * This method does nothing.
   * @param partRef the part that was hidden.
   */
  @Override public void partHidden(IWorkbenchPartReference partRef) {}

  /**
   * This method does nothing.
   * @param partRef the part that is visible.
   */
  @Override public void partVisible(IWorkbenchPartReference partRef) {}

  /**
   * This method does nothing.
   * @param partRef the part whose input was changed.
   */
  @Override public void partInputChanged(IWorkbenchPartReference partRef) {}
}
