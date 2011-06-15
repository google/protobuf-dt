/*
 * Copyright (c) 2011 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.ui.scoping;

import static com.google.eclipse.protobuf.ui.util.ProjectVariable.useProjectName;

import java.util.List;

import org.eclipse.core.filesystem.*;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.emf.common.util.URI;

import com.google.eclipse.protobuf.ui.preferences.paths.DirectoryPath;
import com.google.eclipse.protobuf.ui.preferences.paths.PathsPreferences;
import com.google.eclipse.protobuf.ui.util.*;

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
    List<DirectoryPath> importRoots = preferences.importRoots();
    for (DirectoryPath root : importRoots) {
      String resolved = resolveUri(importUri, root, project);
      if (resolved != null) return resolved;
    }
    for (DirectoryPath root : importRoots) {
      if (!root.isWorkspacePath()) continue;
      String resolved = resolveUriInFileSystem(importUri, mapping.folderLocation(root.value()));
      if (resolved != null) return resolved;
    }
    return null;
  }

  private String resolveUri(String importUri, DirectoryPath importRoot, IProject project) {
    String root = importRoot.value();
    if (importRoot.isWorkspacePath()) return resolveUriInWorkspace(importUri, root, project);
    return resolveUriInFileSystem(importUri, root);
  }

  private String resolveUriInWorkspace(String importUri, String importRoot, IProject project) {
    String path = PREFIX + useProjectName(importRoot, project) + SEPARATOR + importUri;
    boolean exists = resources.fileExists(URI.createURI(path));
    return (exists) ? path : null;
  }

  private String resolveUriInFileSystem(String importUri, String importRoot) {
    IFileSystem fileSystem = EFS.getLocalFileSystem();
    IPath path = new Path(importRoot + SEPARATOR + importUri);
    IFileInfo fileInfo = fileSystem.getStore(path).fetchInfo();
    if (!fileInfo.exists()) return null;
    return URIUtil.toURI(path).toString();
  }
}
