/*
 * Copyright (c) 2012 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.ui.protoc.command;

import static com.google.common.collect.Lists.newArrayList;
import static java.util.Collections.singletonList;
import static org.eclipse.xtext.util.Strings.isEmpty;

import java.io.File;
import java.util.List;

import org.eclipse.core.resources.*;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Function;
import com.google.eclipse.protobuf.ui.preferences.paths.*;

/**
 * @author alruiz@google.com (Alex Ruiz)
 */
class ImportRootsProtocOption {
  private final PathsPreferences preferences;

  private boolean initialized;
  private List<String> importRoots;

  ImportRootsProtocOption(PathsPreferences preferences) {
    this.preferences = preferences;
  }

  public void addOptionToCommand(ProtocCommand command, IFile protoFile) {
    if (!initialized) {
      initialize(protoFile);
    }
    for (String importRoot : importRoots) {
      appendToCommand(command, importRoot);
    }
  }

  private void initialize(IFile protoFile) {
    initialized = true;
    if (!preferences.areFilesInMultipleDirectories()) {
      importRoots = singletonList(singleImportRoot(protoFile));
      return;
    }
    importRoots = newArrayList();
    preferences.applyToEachDirectoryPath(new Function<DirectoryPath, Void>() {
      @Override public Void apply(DirectoryPath path) {
        String location = path.absolutePathInFileSystem();
        if (!isEmpty(location)) {
          importRoots.add(location);
        }
        return null;
      }
    });
  }

  private String singleImportRoot(IFile protoFile) {
    return singleImportRoot(locationOf(preferences.project()), locationOf(protoFile));
  }

  @VisibleForTesting static String singleImportRoot(File projectLocation, File protoFileLocation) {
    if (protoFileLocation.getParentFile().equals(projectLocation)) {
      return projectLocation.toString();
    }
    File current = protoFileLocation;
    while (!current.getParentFile().equals(projectLocation)) {
      current = current.getParentFile();
    }
    return current.toString();
  }

  private File locationOf(IResource resource) {
    return resource.getLocation().toFile();
  }

  private void appendToCommand(ProtocCommand command, String importRoot) {
    command.appendOption("proto_path", importRoot);
  }
}
