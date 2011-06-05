/*
 * Copyright (c) 2011 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.ui.scoping;

import java.util.List;

import org.eclipse.core.filesystem.*;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.emf.common.util.URI;

import com.google.eclipse.protobuf.ui.preferences.paths.DirectoryPath;
import com.google.eclipse.protobuf.ui.preferences.paths.PathsPreferences;
import com.google.eclipse.protobuf.ui.util.Resources;

/**
 * @author alruiz@google.com (Alex Ruiz)
 */
class MultipleDirectoriesFileResolver implements FileResolverStrategy {

  private final PathMapping mapping;
  private final Resources resources;

  MultipleDirectoriesFileResolver(PathMapping mapping, Resources resources) {
    this.mapping = mapping;
    this.resources = resources;
  }

  /** {@inheritDoc} */
  public String resolveUri(String importUri, URI declaringResourceUri, IProject project, PathsPreferences preferences) {
    List<DirectoryPath> directoryPaths = preferences.directoryPaths();
    for (DirectoryPath directoryPath : directoryPaths) {
      String resolved = resolveUri(importUri, directoryPath);
      if (resolved != null) return resolved;
    }
    for (DirectoryPath directoryPath : directoryPaths) {
      if (!directoryPath.isWorkspacePath()) continue;
      String resolved = resolveUriFileSystem(importUri, mapping.folderLocation(directoryPath.value()));
      if (resolved != null) return resolved;
    }
    return null;
  }

  private String resolveUri(String importUri, DirectoryPath importRoot) {
    String root = importRoot.value();
    if (importRoot.isWorkspacePath()) return resolveUriInWorkspace(importUri, root);
    return resolveUriFileSystem(importUri, root);
  }

  private String resolveUriInWorkspace(String importUri, String importRoot) {
    String path = PREFIX + importRoot + SEPARATOR + importUri;
    boolean exists = resources.fileExists(URI.createURI(path));
    return (exists) ? path : null;
  }

  private String resolveUriFileSystem(String importUri, String importRoot) {
    IFileSystem fileSystem = EFS.getLocalFileSystem();
    IPath path = new Path(importRoot + SEPARATOR + importUri);
    IFileInfo fileInfo = fileSystem.getStore(path).fetchInfo();
    if (!fileInfo.exists()) return null;
    return URIUtil.toURI(path).toString();
  }
}
