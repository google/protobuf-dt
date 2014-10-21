/*
 * Copyright (c) 2011, 2014 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.scoping;

import com.google.eclipse.protobuf.scoping.IUriResolver.NullUriResolver;
import com.google.inject.ImplementedBy;

import org.eclipse.core.resources.IProject;
import org.eclipse.emf.common.util.URI;

/**
 * @author alruiz@google.com (Alex Ruiz)
 */
@ImplementedBy(NullUriResolver.class)
public interface IUriResolver {
  /**
   * Returns the resolved URI for the given URI.
   *
   * @param importUri the URI to resolve
   * @param declaringResourceUri the resource the request is initiated from or {@code null}. If
   *        {@code null}, then the request is assumed to be from the top level
   * @param project the project the request is originating from, or {@code null}. If {@code null},
   *        try to resolve with the import paths of all projects in the workspace
   */
  String resolveUri(String importUri, URI declaringResourceUri, IProject project);

  class NullUriResolver implements IUriResolver {
    @Override
    public String resolveUri(String importUri, URI declaringResourceUri, IProject project) {
      throw new UnsupportedOperationException();
    }
  }
}
