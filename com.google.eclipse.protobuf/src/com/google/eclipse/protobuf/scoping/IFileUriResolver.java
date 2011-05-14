/*
 * Copyright (c) 2011 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.scoping;

import org.eclipse.emf.ecore.resource.Resource;

import com.google.eclipse.protobuf.scoping.IFileUriResolver.NullFileUriResolver;
import com.google.inject.ImplementedBy;

/**
 * Resolves partial URIs converting them to full ones belonging to existing files.
 * 
 * @author alruiz@google.com (Alex Ruiz)
 */
@ImplementedBy(NullFileUriResolver.class)
public interface IFileUriResolver {

  /**
   * Resolves the given partial URI.
   * @param importUri the partial URI (comes from a {@code Import}.)
   * @param declaringResource the resource declaring the import.
   * @return the resolved URI.
   */
  String resolveUri(String importUri, Resource declaringResource);
  
  class NullFileUriResolver implements IFileUriResolver {
    public String resolveUri(String importUri, Resource declaringResource) {
      throw new UnsupportedOperationException();
    }
  }
}