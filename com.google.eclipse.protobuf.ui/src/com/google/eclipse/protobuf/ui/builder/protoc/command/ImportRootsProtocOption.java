/*
 * Copyright (c) 2012 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.ui.builder.protoc.command;

import static com.google.common.collect.Lists.newArrayList;
import static com.google.eclipse.protobuf.ui.preferences.paths.core.DirectoryPath.parse;
import static com.google.eclipse.protobuf.ui.util.CommaSeparatedValues.splitCsv;
import static java.util.Collections.emptyList;
import static org.eclipse.xtext.util.Strings.isEmpty;

import java.io.File;
import java.util.List;

import org.eclipse.core.resources.*;

import com.google.eclipse.protobuf.ui.preferences.paths.core.*;

/**
 * @author alruiz@google.com (Alex Ruiz)
 */
class ImportRootsProtocOption {
  private final PathsPreferences preferences;
  private final IProject project;

  private boolean initialized;
  private List<String> importRoots;

  ImportRootsProtocOption(PathsPreferences preferences, IProject project) {
    this.preferences = preferences;
    this.project = project;
  }

  public void appendOptionToCommand(ProtocCommand command, IFile protoFile) {
    if (!initialized) {
      initialize();
    }
    if (!importRoots.isEmpty()) {
      for (String importRoot : importRoots) {
        appendToCommand(command, importRoot);
      }
      return;
    }
    appendToCommand(command, singleImportRoot(protoFile));
  }

  private void initialize() {
    initialized = true;
    if (!preferences.filesInMultipleDirectories().getValue()) {
      importRoots =  emptyList();
      return;
    }
    importRoots = newArrayList();
    String directoryPaths = preferences.directoryPaths().getValue();
    for (String importRoot : splitCsv(directoryPaths)) {
      DirectoryPath path = parse(importRoot, project);
      String location = path.absolutePathInFileSystem();
      if (!isEmpty(location)) {
        importRoots.add(location);
      }
    }
  }

  private String singleImportRoot(IFile protoFile) {
    File projectFile = toFile(project);
    File currentFile = toFile(protoFile);
    while (!currentFile.getParentFile().equals(projectFile)) {
      currentFile = currentFile.getParentFile();
    }
    return currentFile.toString();
  }

  private File toFile(IResource resource) {
    return resource.getLocation().toFile();
  }

  private void appendToCommand(ProtocCommand command, String importRoot) {
    command.appendOption("proto_path", importRoot);
  }
}
