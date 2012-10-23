/*
 * Copyright (c) 2012 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.resource;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;

import com.google.inject.Singleton;

/**
 * Utility methods related to <code>{@link ResourceSet}</code>s.
 *
 * @author alruiz@google.com (Alex Ruiz)
 */
@Singleton public class ResourceSets {

  /**
   * Finds in the given <code>{@link ResourceSet}</code> the resource referred by the given URI.
   * @param resourceSet a collection of resources.
   * @param uri the given URI.
   * @return the resource referred by the given URI, or {@code null} is the given {@code ResourceSet} does
   * not contain the resource.
   */
  public Resource findResource(ResourceSet resourceSet, URI uri) {
    try {
      return resourceSet.getResource(uri, true);
    } catch (Throwable t) {
      return null;
    }
  }
}
