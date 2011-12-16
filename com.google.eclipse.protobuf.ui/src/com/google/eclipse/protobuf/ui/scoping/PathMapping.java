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
import org.eclipse.xtext.util.SimpleCache;

import com.google.common.base.Function;

/**
 * @author alruiz@google.com (Alex Ruiz)
 */
class PathMapping {

  private final SimpleCache<String, String> folderPathMapping = new SimpleCache<String, String>(new FolderPathMapper());

  String folderLocation(String workspacePath) {
    return folderPathMapping.get(workspacePath);
  }

  private static class FolderPathMapper implements Function<String, String> {
    @Override public String apply(String workspacePath) {
      return folder(workspacePath).getLocation().toOSString();
    }

    private static IFolder folder(String workspacePath) {
      IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
      return root.getFolder(new Path(workspacePath));
    }
  }
}
