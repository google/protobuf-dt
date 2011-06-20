/*
 * Copyright (c) 2011 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.ui.validation;

import static com.google.eclipse.protobuf.ui.validation.Validation.validate;

import java.net.URI;

import org.eclipse.core.resources.IProject;
import org.eclipse.ui.*;

import com.google.eclipse.protobuf.ui.util.Resources;
import com.google.inject.*;

/**
 * Triggers validation of .proto files.
 *
 * @author alruiz@google.com (Alex Ruiz)
 */
@Singleton
public class ValidationTrigger {

  private final String PROTO_EDITOR_ID = "com.google.eclipse.protobuf.Protobuf";

  @Inject private Resources resources;

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
    if (!PROTO_EDITOR_ID.equals(editorRef.getId())) return;
    IEditorPart editor = editorRef.getEditor(true);
    IProject fileProject = resources.project(editor);
    if (fileProject == null || !haveEqualUris(project, fileProject)) return;
    validate(editor);
  }

  private boolean haveEqualUris(IProject p1, IProject p2) {
    if (p1 == null || p2 == null) return false;
    URI uri1 = p1.getLocationURI();
    URI uri2 = p2.getLocationURI();
    return areEqual(uri1, uri2);
  }

  private boolean areEqual(URI uri1, URI uri2) {
    if (uri1 == null) return false;
    return uri1.equals(uri2);
  }
}
