/*
 * Copyright (c) 2012 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.ui.editor;

import static org.eclipse.core.runtime.Status.*;

import org.eclipse.core.runtime.*;
import org.eclipse.emf.common.util.URI;
import org.eclipse.xtext.naming.QualifiedName;
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
   * @param query information needed to find the object model to navigate to.
   * @return the result of the operation.
    */
  public IStatus navigateToDefinition(Query query) {
    URI uri = locationLookup.findModelObjectUri(query.qualifiedName, query.filePath);
    if (uri != null) {
      editorOpener.open(uri, true);
      return OK_STATUS;
    }
    return CANCEL_STATUS;
  }

  /**
   * Information needed to find the object model to navigate to.
   *
   * @author alruiz@google.com (Alex Ruiz)
   */
  public static class Query {
    final QualifiedName qualifiedName;
    final IPath filePath;

    /**
     * Creates a new <code>{@link Query}</code>, to be used by
     * <code>{@link ModelObjectDefinitionNavigator#navigateToDefinition(Query)}</code>.
     * @param qualifiedName the qualified name to match.
     * @param filePath the path and name of the file where to perform the lookup.
     * @return the created {@code Query}.
     */
    public static Query query(QualifiedName qualifiedName, IPath filePath) {
      return new Query(qualifiedName, filePath);
    }

    private Query(QualifiedName qualifiedName, IPath filePath) {
      this.qualifiedName = qualifiedName;
      this.filePath = filePath;
    }
  }
}
