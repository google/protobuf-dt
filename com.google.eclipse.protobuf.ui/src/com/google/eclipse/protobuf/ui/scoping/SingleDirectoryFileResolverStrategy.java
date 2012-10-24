/*
 * Copyright (c) 2011 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.ui.scoping;

import static org.eclipse.core.runtime.IPath.SEPARATOR;
import static org.eclipse.emf.common.util.URI.createURI;

import org.eclipse.emf.common.util.URI;

import com.google.eclipse.protobuf.ui.preferences.paths.PathsPreferences;
import com.google.eclipse.protobuf.util.Uris;
import com.google.inject.Inject;

/**
 * @author alruiz@google.com (Alex Ruiz)
 */
class SingleDirectoryFileResolverStrategy implements FileResolverStrategy {
  @Inject private Uris uris;

  @Override
  public String resolveUri(String importUri, URI declaringResourceUri, Iterable<PathsPreferences> allPathPreferences) {
    return resolveUri(createURI(importUri), declaringResourceUri);
  }

  private String resolveUri(URI importUri, URI declaringResourceUri) {
    StringBuilder pathBuilder = new StringBuilder();
    String[] segments = importUri.segments();
    if (segments.length == 0) {
      return null;
    }
    String firstSegment = segments[0];
    for (String segment : uris.segmentsWithoutFileName(declaringResourceUri)) {
      if (segment.equals(firstSegment)) {
        break;
      }
      pathBuilder.append(segment).append(SEPARATOR);
    }
    String resolved = createResolvedUri(pathBuilder.toString(), importUri, declaringResourceUri);
    return uris.referredResourceExists(createURI(resolved)) ? resolved : null;
  }

  private String createResolvedUri(String path, URI importUri, URI declaringResourceUri) {
    StringBuilder uriBuilder = new StringBuilder();
    return uriBuilder.append(uris.prefixOf(declaringResourceUri))
                     .append(SEPARATOR)
                     .append(path)
                     .append(importUri.toString())
                     .toString();
  }
}
