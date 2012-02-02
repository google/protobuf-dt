/*
 * Copyright (c) 2012 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.resource;

import org.eclipse.core.runtime.IPath;
import org.eclipse.emf.common.util.URI;
import org.eclipse.xtext.naming.QualifiedName;
import org.eclipse.xtext.resource.*;

import com.google.common.annotations.VisibleForTesting;
import com.google.eclipse.protobuf.util.IPaths;
import com.google.inject.Inject;

/**
 * Looks up the location of model objects in the Xtext index.
 *
 * @author alruiz@google.com (Alex Ruiz)
 */
public class ModelObjectLocationLookup {
  @Inject private IPaths paths;
  @Inject private IResourceDescriptions xtextIndex;

  /**
   * Finds the URI of a model object whose qualified name matches the given one.
   * @param qualifiedName the qualified name to match.
   * @param filePath the path and name of the file where to perform the lookup.
   * @return the URI  of a model object whose qualified name matches the given one, or {@code null} if a matching model
   * object cannot be found.
   */
  public URI findModelObjectUri(QualifiedName qualifiedName, IPath filePath) {
    for (IResourceDescription resourceDescription : xtextIndex.getAllResourceDescriptions()) {
      URI resourceUri = resourceDescription.getURI();
      if (paths.areReferringToSameFile(filePath, resourceUri)) {
        // we found the resource we are looking for.
        for (IEObjectDescription exported : resourceDescription.getExportedObjects()) {
          QualifiedName modelObjectQualifiedName = exported.getQualifiedName();
          if (qualifiedName.equals(modelObjectQualifiedName)) {
            return exported.getEObjectURI();
          }
        }
        break;
      }
    }
    return null;
  }

  @VisibleForTesting IResourceDescriptions getXtextIndex() {
    return xtextIndex;
  }
}
