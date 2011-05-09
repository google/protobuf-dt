// Copyright 2011 Google Inc. All Rights Reserved.

package com.google.eclipse.protobuf.scoping;

import java.util.List;

import org.eclipse.emf.common.util.URI;

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
  String fixUri(String importUri, URI resourceUri) {
    if (importUri.startsWith(PREFIX)) return importUri;
    String prefix = uriPrefix(URI.createURI(importUri).segmentsList(), resourceUri);
    if (importUri.startsWith(prefix)) return importUri;
    if (!prefix.endsWith(SEPARATOR)) prefix += SEPARATOR;
    String fixed = prefix + importUri;
    System.out.println(resourceUri + " : " + importUri + " : " + fixed);
    return fixed;
  }

  private String uriPrefix(List<String> importUri, URI resourceUri) {
    StringBuilder prefix = new StringBuilder();
    prefix.append(PREFIX);
    String firstSegment = importUri.get(0);
    List<String> segments = resourceUri.segmentsList();
    int end = segments.size() - 1; // ignore file name (at the end of the URI)
    for (int j = 1; j < end; j++) {
      if (segments.get(j).equals(firstSegment)) break;
      prefix.append(SEPARATOR).append(segments.get(j));
    }
    return prefix.toString();
  }
}
