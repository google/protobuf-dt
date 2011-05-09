// Copyright 2011 Google Inc. All Rights Reserved.

package com.google.eclipse.protobuf.scoping;

import static org.eclipse.emf.common.util.URI.createURI;
import static org.eclipse.xtext.util.Tuples.pair;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.emf.common.util.URI;
import org.eclipse.xtext.util.Pair;

/**
 * @author alruiz@google.com (Alex Ruiz)
 */
class ImportUriFixer {
  
  static final String PREFIX = "platform:/resource";

  private static final String SEPARATOR = "/";

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
  String fixUri(String importUri, URI resourceUri, ResourceChecker checker) {
    if (importUri.startsWith(PREFIX)) return importUri;
    Pair<String, List<String>> importUriPair = pair(importUri, createURI(importUri).segmentsList());
    String fixed = fixUri(importUriPair, resourceUri, checker);
    System.out.println(resourceUri + " : " + importUri + " : " + fixed);
    if (fixed == null) return importUri;
    return fixed;
  }
  
  private String fixUri(Pair<String, List<String>> importUri, URI resourceUri, ResourceChecker checker) {
    List<String> segments = resourceUri.segmentsList();
    return fixUri(importUri, removeFirstAndLast(segments), checker);
  }

  private List<String> removeFirstAndLast(List<String> list) {
    if (list.isEmpty()) return list;
    List<String> newList = new ArrayList<String>(list);
    newList.remove(0);
    newList.remove(newList.size() - 1);
    return newList;
  }
  
  private String fixUri(Pair<String, List<String>> importUri, List<String> resourceUri, ResourceChecker checker) {
    StringBuilder prefix = new StringBuilder();
    prefix.append(PREFIX);
    String firstSegment = importUri.getSecond().get(0);
    for (String segment : resourceUri) {
      if (segment.equals(firstSegment)) break;
      prefix.append(SEPARATOR).append(segment);
    }
    prefix.append(SEPARATOR);
    String fixed =  prefix.toString() + importUri.getFirst();
    if (checker.resourceExists(fixed)) return fixed;
    return null;
  }
}
