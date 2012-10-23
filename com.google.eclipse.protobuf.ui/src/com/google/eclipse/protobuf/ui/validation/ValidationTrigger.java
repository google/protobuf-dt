/*
 * Copyright (c) 2011 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.ui.validation;

import static com.google.common.base.Objects.equal;
import static com.google.eclipse.protobuf.ui.validation.ProtobufValidation.validate;

import java.net.URI;

import org.eclipse.core.resources.IProject;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;

import com.google.eclipse.protobuf.ui.util.Editors;
import com.google.inject.Inject;
import com.google.inject.Singleton;

/**
 * Triggers validation of .proto files.
 *
 * @author alruiz@google.com (Alex Ruiz)
 */
@Singleton public class ValidationTrigger {
  private final String PROTO_EDITOR_ID = "com.google.eclipse.protobuf.Protobuf";

  @Inject private Editors editors;

  /**
   * Triggers validation of all open .proto files belonging to the given project.
   * @param project the given project.
   */
  public void validateOpenEditors(IProject project) {
    for (IWorkbenchWindow window : PlatformUI.getWorkbench().getWorkbenchWindows()) {
      for (IWorkbenchPage page : window.getPages()) {
        for (IEditorReference editorRef : page.getEditorReferences()) {
          validateFileInEditor(editorRef, project);
        }
      }
    }
  }

  private void validateFileInEditor(IEditorReference editorRef, IProject project) {
    if (!PROTO_EDITOR_ID.equals(editorRef.getId())) {
      return;
    }
    IEditorPart editor = editorRef.getEditor(true);
    IProject fileProject = editors.projectOwningFileDisplayedIn(editor);
    if (fileProject == null || !haveEqualUris(project, fileProject)) {
      return;
    }
    validate(editor);
  }

  private boolean haveEqualUris(IProject p1, IProject p2) {
    if (p1 == null || p2 == null) {
      return false;
    }
    URI uri1 = p1.getLocationURI();
    URI uri2 = p2.getLocationURI();
    return equal(uri1, uri2);
  }
}
