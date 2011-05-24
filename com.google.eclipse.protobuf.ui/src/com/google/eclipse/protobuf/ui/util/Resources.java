/*
 * Copyright (c) 2011 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.ui.util;

import org.eclipse.core.resources.*;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.emf.common.util.URI;

import com.google.inject.Singleton;

/**
 * Utility methods related to resources (e.g. files, directories.)
 *
 * @author alruiz@google.com (Alex Ruiz)
 */
@Singleton
public class Resources {

  /**
   * Returns the project that contains the resource at the given URI.
   * @param resourceUri the given URI.
   * @return the project that contains the resource at the given URI.
   */
  public IProject project(URI resourceUri) {
    return file(resourceUri).getProject();
  }

  /**
   * Indicates whether the given URI belongs to an existing file.
   * @param fileUri the URI to check, as a {@code String}.
   * @return {@code true} if the given URI belongs to an existing file, {@code false} otherwise.
   */
  public boolean fileExists(URI fileUri) {
    return file(fileUri).exists();
  }

  /**
   * Returns a handle to file identified by the given URI.
   * @param uri the given URI.
   * @return a handle to file identified by the given URI.
   */
  public IFile file(URI uri) {
    IPath resourcePath = pathOf(uri);
    IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
    return root.getFile(resourcePath);
  }

  /**
   * Constructs a new path from the given URI.
   * @param uri the given URI.
   * @return the constructed path.
   */
  public IPath pathOf(URI uri) {
    return new Path(uri.toPlatformString(true));
  }
}
