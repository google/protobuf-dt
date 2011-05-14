/*
 * Copyright (c) 2011 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.ui.scoping;

import static com.google.eclipse.protobuf.ui.preferences.paths.PathsResolutionType.SINGLE_DIRECTORY;
import static org.eclipse.emf.common.util.URI.createURI;
import static org.eclipse.xtext.util.Tuples.pair;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.xtext.util.Pair;

import com.google.eclipse.protobuf.scoping.IFileUriResolver;
import com.google.eclipse.protobuf.ui.preferences.paths.PathsPreferenceReader;
import com.google.eclipse.protobuf.ui.preferences.paths.PathsPreferences;
import com.google.inject.Inject;

/**
 * @author alruiz@google.com (Alex Ruiz)
 */
public class FileUriResolver implements IFileUriResolver {
  
  private static final String PREFIX = "platform:/resource";
  private static final String SEPARATOR = "/";

  @Inject private PathsPreferenceReader preferenceReader;
  @Inject private Resources resources;
  
  /*
   * The import URI is relative to the file where the import is. Protoc works fine, but the editor doesn't.
   * In order for the editor to see the import, we need to add to the import URI "platform:resource" and the parent
   * folder of the file containing the import.
   *
   * For example: given the following file hierarchy:
   *
   * - protobuf-test (project)
   *   - folder
   *     - proto2.proto
   *   - proto1.proto
   *
   * If we import "folder/proto2.proto" into proto1.proto, proto1.proto will compile fine, but the editor will complain.
   * We need to have the import URI as "platform:/resource/protobuf-test/folder/proto2.proto" for the editor to see it.
   */
  public String resolveUri(String importUri, Resource declaringResource) {
    if (importUri.startsWith(PREFIX)) return importUri;
    Pair<String, List<String>> importUriPair = pair(importUri, createURI(importUri).segmentsList());
    String resolved = resolveUri(importUriPair, declaringResource.getURI());
//    System.out.println(declaringResource.getURI() + " : " + importUri + " : " + resolved);
    if (resolved == null) return importUri;
    return resolved;
  }
  
  private String resolveUri(Pair<String, List<String>> importUri, URI resourceUri) {
    IProject project = resources.project(resourceUri);
    PathsPreferences preferences = preferenceReader.readFromPrefereceStore(project);
    List<String> segments = removeFirstAndLast(resourceUri.segmentsList());
    if (preferences.fileResolutionType().equals(SINGLE_DIRECTORY)) {
      return resolveUri(importUri, segments);
    }
    for (String folderName : preferences.folderNames()) {
      String resolved = resolveUri(importUri, folderName, project);
      if (resolved != null) return resolved;
    }
    return null;
  }
  
  private List<String> removeFirstAndLast(List<String> list) {
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
  
  private String resolveUri(Pair<String, List<String>> importUri, String folderName, IProject project) {
    String path = folderName + SEPARATOR + importUri.getFirst();
    IResource findMember = project.findMember(path);
    boolean exists = (findMember != null) ? findMember.exists() : false;
    return (exists) ? PREFIX +  project.getFullPath() + SEPARATOR + path : null;
  }
}
