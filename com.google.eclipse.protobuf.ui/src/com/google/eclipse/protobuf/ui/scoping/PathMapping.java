/*
 * Copyright (c) 2011 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.ui.scoping;

import org.eclipse.core.resources.*;
import org.eclipse.core.runtime.Path;

/**
 * @author alruiz@google.com (Alex Ruiz)
 */
class PathMapping {
  String directoryLocation(String workspacePath) {
    return directory(workspacePath).getLocation().toOSString();
  }

  private static IFolder directory(String workspacePath) {
    IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
    return root.getFolder(new Path(workspacePath));
  }
}
