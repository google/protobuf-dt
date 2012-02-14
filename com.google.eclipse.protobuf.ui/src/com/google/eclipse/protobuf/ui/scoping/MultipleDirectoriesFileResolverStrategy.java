/*
 * Copyright (c) 2011 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.ui.scoping;

import static com.google.common.collect.Lists.newArrayList;
import static com.google.eclipse.protobuf.ui.util.CommaSeparatedValues.splitCsv;
import static com.google.eclipse.protobuf.ui.util.Uris.*;
import static org.eclipse.core.runtime.IPath.SEPARATOR;

import java.util.List;

import org.eclipse.emf.common.util.URI;

import com.google.eclipse.protobuf.ui.preferences.paths.core.*;
import com.google.eclipse.protobuf.ui.util.Uris;
import com.google.inject.Inject;

/**
 * @author alruiz@google.com (Alex Ruiz)
 */
class MultipleDirectoriesFileResolverStrategy implements FileResolverStrategy {
  @Inject private ResourceLocations locations;
  @Inject private Uris uris;

  @Override
  public String resolveUri(String importUri, URI declaringResourceUri, Iterable<PathsPreferences> allPathPreferences) {
    for (PathsPreferences preferences : allPathPreferences) {
      String resolved = resolveUri(importUri, declaringResourceUri, preferences);
      if (resolved != null) {
        return resolved;
      }
    }
    return null;
  }

  private String resolveUri(String importUri, URI declaringResourceUri, PathsPreferences preferences) {
    String directoryPaths = preferences.directoryPaths().getValue();
    List<String> unresolvedWorkspacePaths = newArrayList();
    for (String importRoot : splitCsv(directoryPaths)) {
      DirectoryPath path = DirectoryPath.parse(importRoot, preferences.getProject());
      String resolved = resolveUri(importUri, path);
      if (resolved != null) {
        return resolved;
      }
      if (path.isWorkspacePath()) {
        unresolvedWorkspacePaths.add(path.value());
      }
    }
    for (String root : unresolvedWorkspacePaths) {
      String directoryLocation = locations.directoryLocation(root);
      String resolved = resolveUriInFileSystem(importUri, directoryLocation);
      if (resolved != null) {
        return resolved;
      }
    }
    return null;
  }

  private String resolveUri(String importUri, DirectoryPath importRootPath) {
    URI uri = uri(importUri, importRootPath.value(), importRootPath.isWorkspacePath());
    return resolveUri(uri);
  }

  private String resolveUriInFileSystem(String importUri, String importRootPath) {
    URI uri = uri(importUri, importRootPath, false);
    return resolveUri(uri);
  }

  private URI uri(String importUri, String importRootPath, boolean isWorkspacePath) {
    String prefix = isWorkspacePath ? PLATFORM_RESOURCE_PREFIX : FILE_PREFIX;
    StringBuilder pathBuilder = new StringBuilder().append(prefix)
                                                   .append(importRootPath)
                                                   .append(SEPARATOR)
                                                   .append(importUri);
    String path = pathBuilder.toString();
    return URI.createURI(path);
  }

  private String resolveUri(URI uri) {
    return (uris.referredResourceExists(uri)) ? uri.toString() : null;
  }
}
