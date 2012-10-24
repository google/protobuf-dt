/*
 * Copyright (c) 2011 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.util;

import static java.util.Collections.emptyList;
import static java.util.Collections.unmodifiableList;

import static com.google.common.collect.Lists.newArrayList;
import static com.google.eclipse.protobuf.util.Workspaces.workspaceRoot;

import java.io.File;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.emf.common.util.URI;

import com.google.inject.Singleton;

/**
 * Utility methods related to URIs.
 *
 * @author alruiz@google.com (Alex Ruiz)
 */
@Singleton public class Uris {
  /**
   * Indicates whether the resource at the given URI has extension "proto".
   * @param uri the given URI.
   * @return {@code true} if the resource has extension "proto", {@code false} otherwise.
   */
  public boolean hasProtoExtension(URI uri) {
    return "proto".equals(uri.fileExtension());
  }

  /**
   * Indicates whether the resource or file referred by the given URI exists.
   * @param uri the URI to check. It may be {@code null}.
   * @return {@code true} if the resource or file referred by the given URI exists, {@code false} otherwise.
   */
  public boolean referredResourceExists(URI uri) {
    if (uri == null) {
      return false;
    }
    if (uri.isFile()) {
      File file = new File(uri.path());
      return file.exists();
    }
    if (uri.isPlatformResource()) {
      return referredFileExists(uri);
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

  /**
   * Returns the "prefix" of the given URI as follows:
   * <ul>
   * <li>"platform:/resource", if the URI refers to a platform resource</li>
   * <li>"file:", if the URI refers to a file</li>
   * <li>an empty {@code String} otherwise</li>
   * </ul>
   * @param uri the given URI.
   * @return the "prefix" of the given URI.
   */
  public String prefixOf(URI uri) {
    if (uri.isFile()) {
      return "file:";
    }
    if (uri.isPlatformResource()) {
      return "platform:/resource";
    }
    return "";
  }

  /**
   * Returns the project that contains the file referred by the given URI.
   * @param resourceUri the given URI.
   * @return the project that contains the file referred by the given URI, or {@code null} if the resource referred by
   * the given URI is not a file in the workspace.
   */
  public IProject projectOfReferredFile(URI resourceUri) {
    IFile file = referredFile(resourceUri);
    return (file != null) ? file.getProject() : null;
  }

  /**
   * Indicates whether the given URI refers to an existing file.
   * @param fileUri the URI to check, as a {@code String}.
   * @return {@code true} if the given URI refers to an existing file, {@code false} otherwise.
   */
  public boolean referredFileExists(URI fileUri) {
    IFile file = referredFile(fileUri);
    return (file != null) ? file.exists() : false;
  }

  /**
   * Returns a handle to a workspace file referred by the given URI.
   * @param uri the given URI.
   * @return a handle to a workspace file referred by the given URI or {@code null} if the URI does not refer a
   * workspace file.
   */
  public IFile referredFile(URI uri) {
    IWorkspaceRoot root = workspaceRoot();
    IPath path = pathOf(uri);
    return (path != null) ? root.getFile(path) : null;
  }

  private IPath pathOf(URI uri) {
    String platformString = uri.toPlatformString(true);
    return platformString != null ? Path.fromOSString(platformString) : null;
  }
}
