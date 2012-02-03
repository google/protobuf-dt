/*
 * Copyright (c) 2011 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.model.util;

import static org.eclipse.emf.common.util.URI.createURI;
import static org.eclipse.emf.ecore.util.EcoreUtil.getAllContents;

import org.eclipse.emf.common.util.*;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.*;
import org.eclipse.xtext.parser.IParseResult;
import org.eclipse.xtext.resource.XtextResource;
import org.eclipse.xtext.scoping.impl.ImportUriResolver;

import com.google.eclipse.protobuf.protobuf.*;
import com.google.inject.Inject;

/**
 * Utility methods related to <code>{@link Resource}</code>
 *
 * @author alruiz@google.com (Alex Ruiz)
 */
public class Resources {
  @Inject private ImportUriResolver uriResolver;

  /**
   * Finds in the given <code>{@link ResourceSet}</code> the resource referred by the URI of the given import.
   * @param anImport the given import.
   * @param resourceSet a collection of resources.
   * @return the resource referred by the URI of the given import, or {@code null} is the given {@code ResourceSet} does
   * not contain the resource.
   */
  // TODO move to class ResourceSets
  public Resource importedResource(Import anImport, ResourceSet resourceSet) {
    try {
      URI importUri = createURI(uriResolver.apply(anImport));
      return resourceSet.getResource(importUri, true);
    } catch (Throwable t) {
      return null;
    }
  }

  /**
   * Returns the root element of the given resource.
   * @param resource the given resource.
   * @return the root element of the given resource, or {@code null} if the given resource does not have a root element.
   */
  public Protobuf rootOf(Resource resource) {
    if (resource instanceof XtextResource) {
      IParseResult parseResult = ((XtextResource) resource).getParseResult();
      if (parseResult != null) {
        EObject root = parseResult.getRootASTElement();
        return (Protobuf) root;
      }
    }
    TreeIterator<Object> contents = getAllContents(resource, true);
    if (contents.hasNext()) {
      Object next = contents.next();
      if (next instanceof Protobuf) {
        return (Protobuf) next;
      }
    }
    return null;
  }
}
