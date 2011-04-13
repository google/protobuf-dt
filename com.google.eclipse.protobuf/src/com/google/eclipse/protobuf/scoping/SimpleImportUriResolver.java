/*
 * Copyright (c) 2011 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.scoping;

import java.util.List;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.xtext.scoping.impl.ImportUriResolver;

import com.google.eclipse.protobuf.protobuf.Import;

/**
 * @author alruiz@google.com (Alex Ruiz)
 */
public class SimpleImportUriResolver extends ImportUriResolver {

  private static final String PREFIX = "platform:/resource";

  /**
   * Prefix used by EMF for resource URIs: "platform:/resource/".
   */
  public static final String URI_PREFIX = PREFIX + "/";

  /** {@inheritDoc} */
  @Override public String apply(EObject from) {
    if (from instanceof Import)
      fixUri((Import) from);
    return super.apply(from);
  }

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
  private void fixUri(Import i) {
    String prefix = uriPrefix(i.eResource().getURI());
    String uri = i.getImportURI();
    if (!uri.startsWith(prefix)) {
      if (!uri.startsWith(prefix)) prefix += "/";
      i.setImportURI(prefix + uri);
    }
  }

  private String uriPrefix(URI containerUri) {
    StringBuilder prefix = new StringBuilder();
    prefix.append(PREFIX);
    int start = (containerUri.scheme() == null) ? 0 : 1; // ignore the scheme if present (e.g. "resource")
    List<String> segments = containerUri.segmentsList();
    int end = segments.size() - 1; // ignore file name (at the end of the URI)
    for (int j = start; j < end; j++)
      prefix.append("/").append(segments.get(j));
    return prefix.toString();
  }
}
