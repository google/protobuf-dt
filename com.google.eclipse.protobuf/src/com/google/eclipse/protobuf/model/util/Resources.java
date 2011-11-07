/*
 * Copyright (c) 2011 Google Inc.
 * 
 * All rights reserved. This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License v1.0 which
 * accompanies this distribution, and is available at
 * 
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.model.util;

import static org.eclipse.emf.common.util.URI.createURI;

import com.google.eclipse.protobuf.protobuf.Import;
import com.google.inject.Inject;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.*;
import org.eclipse.xtext.scoping.impl.ImportUriResolver;

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
  public Resource importedResource(Import anImport, ResourceSet resourceSet) {
    try {
      URI importUri = createURI(uriResolver.apply(anImport));
      return resourceSet.getResource(importUri, true);
    } catch (Throwable t) {
      return null;
    }
  }
}
