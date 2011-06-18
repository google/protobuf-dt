/*
 * Copyright (c) 2011 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.ui.builder;

import java.util.*;

import org.eclipse.core.resources.*;
import org.eclipse.core.runtime.*;

import com.google.eclipse.protobuf.ui.preferences.pages.compiler.*;

/**
 * @author alruiz@google.com (Alex Ruiz)
 */
class OutputDirectories {

  private static final NullProgressMonitor NO_MONITOR = new NullProgressMonitor();

  private final Map<SupportedLanguage, IFolder> outputDirectories = new HashMap<SupportedLanguage, IFolder>();

  static OutputDirectories findOrCreateOutputDirectories(IProject project, List<CodeGeneration> preferences)
      throws CoreException {
    Map<SupportedLanguage, IFolder> outputDirectories = new HashMap<SupportedLanguage, IFolder>();
    for (CodeGeneration preference : preferences) {
      if (!preference.isEnabled()) continue;
      outputDirectories.put(preference.language(), findOrCreateOutputDirectory(project, preference));
    }
    return new OutputDirectories(outputDirectories);
  }

  private static IFolder findOrCreateOutputDirectory(IProject project, CodeGeneration preference)
      throws CoreException {
    return findOrCreateOutputDirectory(project, preference.outputDirectory());
  }

  private static IFolder findOrCreateOutputDirectory(IProject project, String outputFolderName) throws CoreException {
    IFolder outputFolder = project.getFolder(outputFolderName);
    if (!outputFolder.exists()) outputFolder.create(true, true, NO_MONITOR);
    return outputFolder;
  }

  private OutputDirectories(Map<SupportedLanguage, IFolder> outputDirectories) {
    this.outputDirectories.putAll(outputDirectories);
  }

  Collection<IFolder> values() {
    return outputDirectories.values();
  }

  IFolder outputDirectoryFor(SupportedLanguage language) {
    return outputDirectories.get(language);
  }
}
