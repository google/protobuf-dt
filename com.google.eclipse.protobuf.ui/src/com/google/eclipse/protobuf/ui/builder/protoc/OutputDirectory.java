/*
 * Copyright (c) 2011 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.ui.builder.protoc;

import static com.google.eclipse.protobuf.ui.util.Paths.segmentsOf;
import static java.io.File.separator;

import org.eclipse.core.resources.*;
import org.eclipse.core.runtime.*;

import com.google.eclipse.protobuf.ui.preferences.*;

/**
 * @author alruiz@google.com (Alex Ruiz)
 */
class OutputDirectory {
  private static final NullProgressMonitor NO_MONITOR = new NullProgressMonitor();

  private final boolean enabled;
  private final IFolder location;

  OutputDirectory(IProject project, BooleanPreference codeGenerationEnabled, StringPreference outputDirectory)
      throws CoreException {
    enabled = codeGenerationEnabled.getValue();
    location = findOrCreateLocation(project, outputDirectory.getValue());
  }

  private IFolder findOrCreateLocation(IProject project, String directoryName) throws CoreException {
    IFolder directory = null;
    if (enabled) {
      StringBuilder path = new StringBuilder();
      for (String segment : segmentsOf(directoryName)) {
        path.append(segment);
        directory = project.getFolder(path.toString());
        if (!directory.exists()) {
          directory.create(true, true, NO_MONITOR);
        }
        path.append(separator);
      }
    }
    return directory;
  }

  boolean isEnabled() {
    return enabled;
  }

  IFolder getLocation() {
    return location;
  }
}
