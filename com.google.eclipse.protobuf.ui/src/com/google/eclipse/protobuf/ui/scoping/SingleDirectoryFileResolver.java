/*
 * Copyright (c) 2011 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.ui.scoping;

import static org.eclipse.emf.common.util.URI.createURI;
import static org.eclipse.xtext.util.Tuples.pair;

import java.util.*;

import org.eclipse.core.resources.IProject;
import org.eclipse.emf.common.util.URI;
import org.eclipse.xtext.util.Pair;

import com.google.eclipse.protobuf.ui.preferences.paths.PathsPreferences;

/**
 * @author alruiz@google.com (Alex Ruiz)
 */
class SingleDirectoryFileResolver implements FileResolverStrategy {

  private final Resources resources;

  SingleDirectoryFileResolver(Resources resources) {
    this.resources = resources;
  }

  public String resolveUri(String importUri, URI declaringResourceUri, PathsPreferences preferences, IProject project) {
    List<String> resourceUriSegments = removeFirstAndLast(declaringResourceUri.segmentsList());
    Pair<String, List<String>> importUriPair = pair(importUri, createURI(importUri).segmentsList());
    return resolveUri(importUriPair, resourceUriSegments);
  }

  // first is always "platform" and last is the file name (both unnecessary)
  private static List<String> removeFirstAndLast(List<String> list) {
    if (list.isEmpty()) return list;
    List<String> newList = new ArrayList<String>(list);
    newList.remove(0);
    newList.remove(newList.size() - 1);
    return newList;
  }

  private String resolveUri(Pair<String, List<String>> importUri, List<String> resourceUri) {
    StringBuilder pathBuilder = new StringBuilder();
    String firstSegment = importUri.getSecond().get(0);
    for (String segment : resourceUri) {
      if (segment.equals(firstSegment)) break;
      pathBuilder.append(segment).append(SEPARATOR);
    }
    String resolved = PREFIX + SEPARATOR + pathBuilder.toString() + importUri.getFirst();
    return (resources.fileExists(createURI(resolved))) ? resolved : null;
  }

}
