/*
 * Copyright (c) 2011 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.ui.scoping;

import org.eclipse.emf.common.util.URI;

import com.google.eclipse.protobuf.ui.preferences.paths.DirectoryPath;
import com.google.eclipse.protobuf.ui.preferences.paths.PathsPreferences;

/**
 * @author alruiz@google.com (Alex Ruiz)
 */
class MultipleDirectoriesFileResolver implements FileResolverStrategy {

  private final Resources resources;

  MultipleDirectoriesFileResolver(Resources resources) {
    this.resources = resources;
  }

  /** {@inheritDoc} */
  public String resolveUri(String importUri, URI declaringResourceUri, PathsPreferences preferences) {
    for (DirectoryPath directoryPath : preferences.directoryPaths()) {
      if (!directoryPath.isWorkspacePath()) continue; // TODO file system is not supported yet.
      String resolved = resolveUriInWorkspace(importUri, directoryPath.value());
      if (resolved != null) return resolved;
    }
    return null;
  }

  private String resolveUriInWorkspace(String importUri, String directoryName) {
    String path = PREFIX + directoryName + SEPARATOR + importUri;
    boolean exists = resources.fileExists(URI.createURI(path));
    return (exists) ? path : null;
  }
}
