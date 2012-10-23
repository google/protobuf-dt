/*
 * Copyright (c) 2012 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.ui.util;

import static com.google.eclipse.protobuf.util.Workspaces.workspaceRoot;

import org.eclipse.core.resources.IFolder;
import org.eclipse.core.runtime.IPath;

/**
 * Utility methods related to <code>{@link IPath}</code>s.
 *
 * @author alruiz@google.com (Alex Ruiz)
 */
public final class IPaths {

  /**
   * Returns the absolute path in the local file system of a directory in the workspace. The returned value uses the
   * platform-dependent path separator defined by {@code java.io.File}.
   * @param path the path of the directory. It can be relative or absolute.
   * @return the absolute path in the local file system of a directory.
   */
  public static String directoryLocationInWorkspace(IPath path) {
    IFolder directory = workspaceRoot().getFolder(path);
    return directory.getLocation().toOSString();
  }

  private IPaths() {}
}
