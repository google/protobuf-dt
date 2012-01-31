/*
 * Copyright (c) 2011 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.ui.util;

import static com.google.common.collect.Lists.newArrayList;
import static java.util.Collections.*;

import com.google.inject.*;

import org.eclipse.emf.common.util.URI;

import java.io.File;
import java.util.List;

/**
 * Utility methods related to URIs.
 *
 * @author alruiz@google.com (Alex Ruiz)
 */
@Singleton public class Uris {
  public static String PLATFORM_RESOURCE_PREFIX = "platform:/resource";
  public static String FILE_PREFIX = "file:";

  @Inject private Resources resources;

  /**
   * Indicates whether the resource or file referred by the given URI exists.
   * @param uri the URI to check.
   * @return {@code true} if the resource or file referred by the given URI exists, {@code false} otherwise.
   */
  public boolean exists(URI uri) {
    if (uri.isFile()) {
      File file = new File(uri.path());
      return file.exists();
    }
    if (uri.isPlatformResource()) {
      return resources.fileExists(uri);
    }
    return false;
  }

  /**
   * Returns the segments of the given URI without the file name (last segment.)
   * @param uri the give URI.
   * @return the segments of the given URI without the file name (last segment.)
   */
  public List<String> segmentsWithoutFileName(URI uri) {
    List<String> originalSegments = uri.segmentsList();
    if (originalSegments.isEmpty()) {
      return emptyList();
    }
    List<String> segments = newArrayList(originalSegments);
    if (uri.isPlatformResource()) {
      segments.remove(0);
    }
    segments.remove(segments.size() - 1);
    return unmodifiableList(segments);
  }

  public String prefixOf(URI uri) {
    if (uri.isFile()) {
      return FILE_PREFIX;
    }
    if (uri.isPlatformResource()) {
      return PLATFORM_RESOURCE_PREFIX;
    }
    return "";
  }
}
