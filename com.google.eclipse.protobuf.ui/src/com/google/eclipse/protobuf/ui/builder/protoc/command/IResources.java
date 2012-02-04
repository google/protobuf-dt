/*
 * Copyright (c) 2012 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.ui.builder.protoc.command;

import static com.google.eclipse.protobuf.ui.util.Paths.segmentsOf;
import static org.eclipse.core.runtime.IPath.SEPARATOR;

import org.eclipse.core.resources.*;
import org.eclipse.core.runtime.*;

/**
 * @author alruiz@google.com (Alex Ruiz)
 */
final class IResources {
  private static final NullProgressMonitor NO_MONITOR = new NullProgressMonitor();

  static String locationOf(IFolder directory) {
    return directory.getLocation().toOSString();
  }

  static IFolder findOrCreateDirectory(String directoryName, IProject project) throws CoreException {
    IFolder directory = null;
    StringBuilder path = new StringBuilder();
    for (String segment : segmentsOf(directoryName)) {
      path.append(segment);
      directory = project.getFolder(path.toString());
      if (!directory.exists()) {
        directory.create(true, true, NO_MONITOR);
      }
      path.append(SEPARATOR);
    }
    return directory;
  }

  private IResources() {}
}
