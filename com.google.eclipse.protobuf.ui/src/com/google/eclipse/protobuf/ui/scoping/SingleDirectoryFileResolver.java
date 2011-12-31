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
import static java.util.Collections.*;
import static org.eclipse.emf.common.util.URI.createURI;

import java.util.List;

import org.eclipse.emf.common.util.URI;

import com.google.eclipse.protobuf.ui.preferences.paths.core.PathsPreferences;
import com.google.eclipse.protobuf.ui.util.Resources;

/**
 * @author alruiz@google.com (Alex Ruiz)
 */
class SingleDirectoryFileResolver implements FileResolverStrategy {
  private final Resources resources;

  SingleDirectoryFileResolver(Resources resources) {
    this.resources = resources;
  }

  @Override public String resolveUri(String importUri, URI declaringResourceUri, PathsPreferences preferences) {
    return resolveUri(createURI(importUri), declaringResourceUri);
  }

  private String resolveUri(URI importUri, URI declaringResourceUri) {
    StringBuilder pathBuilder = new StringBuilder();
    String[] segments = importUri.segments();
    if (segments.length == 0) {
      return null;
    }
    String firstSegment = segments[0];
    for (String segment : removeFirstAndLast(declaringResourceUri)) {
      if (segment.equals(firstSegment)) {
        break;
      }
      pathBuilder.append(segment).append(SEPARATOR);
    }
    String resolved = createResolvedUri(pathBuilder.toString(), importUri);
    return fileExists(resolved) ? resolved : null;
  }

  private String createResolvedUri(String path, URI importUri) {
    StringBuilder uriBuilder = new StringBuilder();
    return uriBuilder.append(PLATFORM_RESOURCE_PREFIX)
                     .append(SEPARATOR)
                     .append(path)
                     .append(importUri.toString())
                     .toString();
  }

  private boolean fileExists(String uri) {
    return resources.fileExists(createURI(uri));
  }

  // first is always "platform" and last is the file name (both unnecessary)
  private static List<String> removeFirstAndLast(URI declaringResourceUri) {
    List<String> originalSegments = declaringResourceUri.segmentsList();
    if (originalSegments.isEmpty()) {
      return emptyList();
    }
    List<String> segments = newArrayList(originalSegments);
    segments.remove(0);
    segments.remove(segments.size() - 1);
    return unmodifiableList(segments);
  }
}
