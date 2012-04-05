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

import com.google.inject.ImplementedBy;

/**
 * Indicates whether the resource should be parsed and validated.
 *
 * @author alruiz@google.com (Alex Ruiz)
 */
@ImplementedBy(IResourceVerifier.ResourceVerifier.class)
public interface IResourceVerifier {
  boolean shouldIgnore(URI uri);

  class ResourceVerifier implements IResourceVerifier {
    @Override public boolean shouldIgnore(URI uri) {
      return false;
    }
  }
}