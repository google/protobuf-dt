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
import org.eclipse.xtext.resource.*;

import com.google.common.annotations.VisibleForTesting;
import com.google.eclipse.protobuf.util.IPaths;
import com.google.inject.Inject;

/**
 * Simplified Xtext index lookups.
 *
 * @author alruiz@google.com (Alex Ruiz)
 */
public class IndexLookup {
  @Inject private IPaths paths;
  @Inject private IResourceDescriptions xtextIndex;

  /**
   * Finds the resource description for the given path.
   * @param path the given path.
   * @return the found resource description, or {@code null} if a resource description with a matching path could not be
   * found.
   */
  public IResourceDescription resourceIn(IPath path) {
    IResourceDescription description = lookup(path);
    if (description != null) {
      return description;
    }
    return segmentMatching(path);
  }

  private IResourceDescription lookup(IPath path) {
    URI uri = URI.createPlatformResourceURI(path.toOSString(), false);
    return xtextIndex.getResourceDescription(uri);
  }

  private IResourceDescription segmentMatching(IPath path) {
    for (IResourceDescription description : xtextIndex.getAllResourceDescriptions()) {
      URI resourceUri = description.getURI();
      if (paths.areReferringToSameFile(path, resourceUri)) {
        return description;
      }
    }
    return null;
  }

  @VisibleForTesting IResourceDescriptions getXtextIndex() {
    return xtextIndex;
  }
}
