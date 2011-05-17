/*
 * Copyright (c) 2011 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.ui.scoping;

import org.eclipse.core.filesystem.*;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
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
      String resolved = resolveUri(importUri, directoryPath);
      if (resolved != null) return resolved;
    }
    return null;
  }

  private String resolveUri(String importUri, DirectoryPath directoryPath) {
    if (directoryPath.isWorkspacePath()) return resolveUriInWorkspace(importUri, directoryPath.value());
    return resolveUriFileSystem(importUri, directoryPath.value());
  }

  private String resolveUriInWorkspace(String importUri, String directoryPath) {
    String path = PREFIX + directoryPath + SEPARATOR + importUri;
    boolean exists = resources.fileExists(URI.createURI(path));
    return (exists) ? path : null;
  }

  private String resolveUriFileSystem(String importUri, String directoryPath) {
    IFileSystem fileSystem = EFS.getLocalFileSystem();
    IPath path = new Path(directoryPath + SEPARATOR + importUri);
    IFileInfo fileInfo = fileSystem.getStore(path).fetchInfo();
    if (!fileInfo.exists()) return null;
    return URIUtil.toURI(path).toString();
  }
}
