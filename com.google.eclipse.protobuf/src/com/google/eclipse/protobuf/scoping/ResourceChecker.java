/*
 * Copyright (c) 2011 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.scoping;

import static org.eclipse.emf.common.util.URI.createURI;

import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;

/**
 * Verifies that resources exist.
 *  
 * @author alruiz@google.com (Alex Ruiz)
 */
public class ResourceChecker {

  private final ResourceSet resourceSet;

  public ResourceChecker(ResourceSet resourceSet) {
    this.resourceSet = resourceSet;
  }
  
  /**
   * Indicates whether the given URI belongs to an existing resource.
   * @param uri the URI to check, as a {@code String}.
   * @return {@code true} if the given URI belongs to an existing resource, {@code false} otherwise.
   */
  public boolean resourceExists(String uri) {
    Resource resource = resourceSet.getResource(createURI(uri), true);
    return resource != null && resource.getErrors().isEmpty();
  }
}
