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

import java.util.List;

import org.eclipse.emf.common.util.URI;

import com.google.common.base.Function;
import com.google.eclipse.protobuf.ui.preferences.paths.DirectoryPath;
import com.google.eclipse.protobuf.ui.preferences.paths.PathsPreferences;
import com.google.inject.Inject;

/**
 * @author alruiz@google.com (Alex Ruiz)
 */
class MultipleDirectoriesFileResolverStrategy implements FileResolverStrategy {
  @Inject private UriResolver uriResolver;
  @Inject private ResourceLocations locations;

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

  private String resolveUri(final String importUri, URI declaringResourceUri, PathsPreferences preferences) {
    final List<String> unresolvedWorkspacePaths = newArrayList();
    String resolved = preferences.applyToEachDirectoryPath(new Function<DirectoryPath, String>() {
      @Override public String apply(DirectoryPath path) {
        String uri = uriResolver.resolveUri(importUri, path);
        if (uri == null && path.isWorkspacePath()) {
          unresolvedWorkspacePaths.add(path.value());
        }
        return uri;
      }
    });
    if (resolved != null) {
      return resolved;
    }
    for (String root : unresolvedWorkspacePaths) {
      String directoryLocation = locations.directoryLocation(root);
      String uri = uriResolver.resolveUriInFileSystem(importUri, directoryLocation);
      if (uri != null) {
        return uri;
      }
    }
    return null;
  }
}
