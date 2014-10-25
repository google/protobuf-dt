/*
 * Copyright (c) 2011 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.ui.preferences.paths;

import static com.google.eclipse.protobuf.ui.preferences.paths.ProjectVariable.replaceProjectVariableWithProjectName;
import static com.google.eclipse.protobuf.ui.util.IPaths.directoryLocationInWorkspace;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.core.filesystem.EFS;
import org.eclipse.core.filesystem.IFileInfo;
import org.eclipse.core.filesystem.IFileSystem;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;

/**
 * Represents the path of a directory.
 *
 * @author alruiz@google.com (Alex Ruiz)
 */
public class DirectoryPath {
  private static final Pattern WORKSPACE_PATH_PATTERN = Pattern.compile("\\$\\{workspace_loc:(.*)\\}");

  private final String value;
  private final boolean isWorkspacePath;

  /**
   * Creates a new <code>{@link DirectoryPath}</code>.
   * @param path the path to parse. If the path belongs to a file in the workspace it should match the pattern
   * "\$\{workspace_loc:(.*)\}".
   * @param project the current project.
   * @return the created {@code DirectoryPath}.
   */
  public static DirectoryPath parse(String path, IProject project) {
    Matcher matcher = WORKSPACE_PATH_PATTERN.matcher(path);
    String actualPath = path;
    boolean isWorkspacePath = false;
    if (matcher.matches()) {
      actualPath = matcher.group(1);
      isWorkspacePath = true;
      if (project != null) {
        IPath newPath = replaceProjectVariableWithProjectName(Path.fromOSString(actualPath), project);
        // Issue 204: we'll create an URI from this path. It must not have OS-specific path separators.
        actualPath = newPath.toPortableString();
      }
    }
    return new DirectoryPath(actualPath, isWorkspacePath);
  }

  private DirectoryPath(String path, boolean isWorkspacePath) {
    this.value = path;
    this.isWorkspacePath = isWorkspacePath;
  }

  /**
   * Returns the textual value of this path.
   * @return the textual value of this path.
   */
  public String value() {
    return value;
  }

  /**
   * Indicates whether this path belongs to a workspace resource.
   * @return {@code true} if this path belongs to a workspace resource, {@code false} otherwise.
   */
  public boolean isWorkspacePath() {
    return isWorkspacePath;
  }

  /**
   * Returns the absolute path in the local file system, or {@code null} if no path can be determined.
   * @return the absolute path in the local file system, or {@code null} if no path can be determined.
   */
  public String absolutePathInFileSystem() {
    IPath path = Path.fromOSString(value);
    if (isWorkspacePath()) {
      return directoryLocationInWorkspace(path);
    }
    return locationInFileSystem(path);
  }

  private String locationInFileSystem(IPath path) {
    IFileSystem fileSystem = EFS.getLocalFileSystem();
    IFileInfo fileInfo = fileSystem.getStore(path).fetchInfo();
    if (!fileInfo.isDirectory()) {
      return null;
    }
    return value;
  }
}
