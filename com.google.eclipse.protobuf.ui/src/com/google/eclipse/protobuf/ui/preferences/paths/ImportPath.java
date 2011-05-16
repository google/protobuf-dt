/*
 * Copyright (c) 2011 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.ui.preferences.paths;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author alruiz@google.com (Alex Ruiz)
 */
class ImportPath {

  private static final Pattern WORKSPACE_PATH_PATTERN = Pattern.compile("\\$\\{workspace_loc:(.*)\\}");
  
  final String value;
  final boolean isWorkspacePath;

  static ImportPath parse(String path) {
    Matcher matcher = WORKSPACE_PATH_PATTERN.matcher(path);
    if (matcher.matches()) return new ImportPath(matcher.group(1), true);
    return new ImportPath(path, false);
  }
  
  ImportPath(String path, boolean isWorkspacePath) {
    this.value = path;
    this.isWorkspacePath = isWorkspacePath;
  }
  
  /** {@inheritDoc} */
  @Override public String toString() {
    if (!isWorkspacePath) return value;
    return "${workspace_loc:" + value + "}";
  }
}
