/*
 * Copyright (c) 2012 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.ui.editor;

import org.eclipse.emf.common.util.URI;
import org.eclipse.xtext.ui.editor.IURIEditorOpener;

import com.google.eclipse.protobuf.resource.ModelObjectLocationLookup;
import com.google.inject.Inject;

/**
 * Navigates to the definition of a model object, opening necessary files if necessary.
 *
 * @author alruiz@google.com (Alex Ruiz)
 */
public class ModelObjectDefinitionNavigator {
  @Inject private ModelObjectLocationLookup locationLookup;
  @Inject private IURIEditorOpener editorOpener;

  /**
   * Navigates to the definition of the model object whose qualified name matches the given one. This method will open
   * the file containing the model object definition if necessary.
   * @param qualifiedNameAsText the qualified name to match.
   * @param filePath the path and name of the file where to perform the lookup. It should not include the host.
    */
  public void navigateToDefinition(String qualifiedNameAsText, String filePath) {
    URI uri = locationLookup.findModelObjectUri(qualifiedNameAsText, filePath);
    if (uri != null) {
      editorOpener.open(uri, true);
    }
  }
}
