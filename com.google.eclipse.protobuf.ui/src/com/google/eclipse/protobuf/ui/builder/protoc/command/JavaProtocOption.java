/*
 * Copyright (c) 2012 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.ui.builder.protoc.command;

import static com.google.eclipse.protobuf.ui.builder.protoc.command.IResources.*;

import org.eclipse.core.resources.*;
import org.eclipse.core.runtime.CoreException;

import com.google.eclipse.protobuf.ui.preferences.compiler.core.CompilerPreferences;

/**
 * @author alruiz@google.com (Alex Ruiz)
 */
class JavaProtocOption implements OutputDirectoryProtocOption {
  private final CompilerPreferences preferences;
  private final IProject project;

  private boolean initialized;
  private boolean enabled;
  private IFolder outputDirectory;
  private String outputDirectoryLocation;

  JavaProtocOption(CompilerPreferences preferences, IProject project) {
    this.preferences = preferences;
    this.project = project;
  }

  @Override public void appendOptionToCommand(ProtocCommand command) throws CoreException {
    ensureIsInitialized();
    if (enabled) {
      command.appendOption("java_out", outputDirectoryLocation);
    }
  }

  @Override public IFolder outputDirectory() throws CoreException {
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
    enabled = preferences.javaCodeGenerationEnabled().getValue();
    if (enabled) {
      String directoryName = preferences.javaOutputDirectory().getValue();
      outputDirectory = findOrCreateDirectory(directoryName, project);
      outputDirectoryLocation = locationOf(outputDirectory);
    }
  }
}
