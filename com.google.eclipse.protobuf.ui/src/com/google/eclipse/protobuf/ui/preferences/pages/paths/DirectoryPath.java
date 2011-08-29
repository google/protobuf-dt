/*
 * Copyright (c) 2011 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.ui.preferences.pages.paths;

import static com.google.eclipse.protobuf.ui.preferences.pages.paths.ProjectVariable.useProjectName;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.core.filesystem.*;
import org.eclipse.core.resources.*;
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

  static DirectoryPath parse(String path) {
    return parse(path, null);
  }

  static DirectoryPath parse(String path, IProject project) {
    Matcher matcher = WORKSPACE_PATH_PATTERN.matcher(path);
    if (matcher.matches()) {
      String actualPath = matcher.group(1);
      if (project != null) actualPath = useProjectName(actualPath, project);
      return new DirectoryPath(actualPath, true);
    }
    return new DirectoryPath(path, false);
  }

  DirectoryPath(String path, boolean isWorkspacePath) {
    this.value = path;
    this.isWorkspacePath = isWorkspacePath;
  }

  /** {@inheritDoc} */
  @Override public String toString() {
    if (!isWorkspacePath) return value;
    return "${workspace_loc:" + value + "}";
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
   * @param project used if this path belongs to a workspace resource.
   * @return the absolute path in the local file system, or {@code null} if no path can be determined.
   */
  public String location(IProject project) {
    if (isWorkspacePath()) return locationOfWorkspaceDirectory(project);
    return locationOfFileSystemDirectory();
  }

  private String locationOfWorkspaceDirectory(IProject project) {
    IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
    IFolder folder = root.getFolder(new Path(value()));
    return folder.getLocation().toOSString();
  }

  private String locationOfFileSystemDirectory() {
    IFileSystem fileSystem = EFS.getLocalFileSystem();
    IFileInfo fileInfo = fileSystem.getStore(new Path(value())).fetchInfo();
    if (!fileInfo.isDirectory()) return null;
    return value();
  }
}
