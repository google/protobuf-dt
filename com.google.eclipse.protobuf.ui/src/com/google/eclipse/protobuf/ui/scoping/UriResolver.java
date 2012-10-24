/*
 * Copyright (c) 2012 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.ui.scoping;

import static org.eclipse.core.runtime.IPath.SEPARATOR;
import static org.eclipse.xtext.util.Strings.isEmpty;

import org.eclipse.emf.common.util.URI;

import com.google.eclipse.protobuf.ui.preferences.paths.DirectoryPath;
import com.google.eclipse.protobuf.util.Uris;
import com.google.inject.Inject;
import com.google.inject.Singleton;

/**
 * @author alruiz@google.com (Alex Ruiz)
 */
@Singleton class UriResolver {
  private static final String PATH_SEPARATOR = new String(new char[] { SEPARATOR });

  @Inject private FileSystemPathResolver pathResolver;
  @Inject private Uris uris;

  String resolveUri(String importUri, DirectoryPath importRootPath) {
    URI uri = resolveUri(importUri, importRootPath.value(), importRootPath.isWorkspacePath());
    return resolveUri(uri);
  }

  String resolveUriInFileSystem(String importUri, String importRootPath) {
    URI uri = resolveFileUri(importUri, importRootPath);
    return resolveUri(uri);
  }

  private URI resolveUri(String importUri, String importRootPath, boolean isWorkspacePath) {
    if (isWorkspacePath) {
      return resolvePlatformResourceUri(importUri, importRootPath);
    }
    return resolveFileUri(importUri, importRootPath);
  }

  private URI resolvePlatformResourceUri(String importUri, String importRootPath) {
    String path = buildUriPath(importUri, importRootPath);
    return URI.createPlatformResourceURI(path, true);
  }

  private URI resolveFileUri(String importUri, String importRootPath) {
    String resolvedImportRootPath = pathResolver.resolvePath(importRootPath);
    if (isEmpty(resolvedImportRootPath)) {
      return null;
    }
    String path = buildUriPath(importUri, resolvedImportRootPath);
    return URI.createFileURI(path);
  }

  private String buildUriPath(String importUri, String importRootPath) {
    StringBuilder pathBuilder = new StringBuilder().append(importRootPath);
    if (!importRootPath.endsWith(PATH_SEPARATOR)) {
      pathBuilder.append(PATH_SEPARATOR);
    }
    pathBuilder.append(importUri);
    return pathBuilder.toString();
  }

  private String resolveUri(URI uri) {
    return (uris.referredResourceExists(uri)) ? uri.toString() : null;
  }
}
