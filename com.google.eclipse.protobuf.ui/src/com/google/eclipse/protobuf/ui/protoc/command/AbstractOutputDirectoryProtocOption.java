/*
 * Copyright (c) 2012 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.ui.protoc.command;

import static java.io.File.separator;
import static org.eclipse.core.runtime.IPath.SEPARATOR;

import org.eclipse.core.resources.*;
import org.eclipse.core.runtime.*;

import com.google.common.annotations.VisibleForTesting;
import com.google.eclipse.protobuf.ui.preferences.*;
import com.google.eclipse.protobuf.ui.preferences.compiler.core.CompilerPreferences;

/**
 * @author alruiz@google.com (Alex Ruiz)
 */
abstract class AbstractOutputDirectoryProtocOption implements ProtocOption {
  private static final NullProgressMonitor NO_MONITOR = new NullProgressMonitor();

  private final String optionName;
  private final CompilerPreferences preferences;
  private final IProject project;

  private boolean initialized;
  private boolean enabled;
  private IFolder outputDirectory;
  private String outputDirectoryLocation;

  AbstractOutputDirectoryProtocOption(String optionName, CompilerPreferences preferences, IProject project) {
    this.optionName = optionName;
    this.preferences = preferences;
    this.project = project;
  }

  @Override public final void addOptionTo(ProtocCommand command) throws CoreException {
    ensureIsInitialized();
    if (enabled) {
      command.appendOption(optionName, outputDirectoryLocation);
    }
  }

  final IFolder outputDirectory() throws CoreException {
    ensureIsInitialized();
    return outputDirectory;
  }

  private void ensureIsInitialized() throws CoreException {
    if (!initialized) {
      initialize();
    }
  }

  private void initialize() throws CoreException {
    initialized = true;
    enabled = isEnabled(preferences).getValue();
    if (enabled) {
      String directoryName = outputDirectoryName(preferences).getValue();
      outputDirectory = findOrCreateDirectory(directoryName);
      outputDirectoryLocation = outputDirectory.getLocation().toOSString();
    }
  }

  private IFolder findOrCreateDirectory(String directoryName) throws CoreException {
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

  @VisibleForTesting static String[] segmentsOf(String path) {
    if (path == null) {
      throw new NullPointerException("The given path should not be null");
    }
    return path.split("\\Q" + separator + "\\E");
  }

  abstract BooleanPreference isEnabled(CompilerPreferences p);
  abstract StringPreference outputDirectoryName(CompilerPreferences p);
}
