/*
 * Copyright (c) 2011 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.ui.preferences.paths;

import static com.google.eclipse.protobuf.ui.util.ProjectVariable.containsProjectVariable;

import java.util.regex.*;

/**
 * @author alruiz@google.com (Alex Ruiz)
 */
public class DirectoryPath {

  private static final Pattern WORKSPACE_PATH_PATTERN = Pattern.compile("\\$\\{workspace_loc:(.*)\\}");
  
  private final String value;
  private final boolean isWorkspacePath;

  static DirectoryPath parse(String path) {
    Matcher matcher = WORKSPACE_PATH_PATTERN.matcher(path);
    if (matcher.matches()) return new DirectoryPath(matcher.group(1), true);
    if (containsProjectVariable(path)) return new DirectoryPath(path, true);
    return new DirectoryPath(path, false);
  }
  
  DirectoryPath(String path, boolean isWorkspacePath) {
    this.value = path;
    this.isWorkspacePath = isWorkspacePath;
  }
  
  /** {@inheritDoc} */
  @Override public String toString() {
    if (!isWorkspacePath || containsProjectVariable(value)) return value;
    return "${workspace_loc:" + value + "}";
  }

  public String value() {
    return value;
  }

  public boolean isWorkspacePath() {
    return isWorkspacePath;
  }
}
