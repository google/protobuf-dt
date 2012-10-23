/*
 * Copyright (c) 2012 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.ui.protoc.command;

import static org.eclipse.core.resources.IResource.CHECK_ANCESTORS;
import static org.eclipse.xtext.util.Strings.isEmpty;

import static com.google.common.collect.Lists.newArrayList;

import java.io.File;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IPath;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Function;
import com.google.eclipse.protobuf.ui.preferences.paths.DirectoryPath;
import com.google.eclipse.protobuf.ui.preferences.paths.PathsPreferences;

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
    importRoots = newArrayList();
    if (!preferences.areFilesInMultipleDirectories()) {
      if (protoFile.isLinked(CHECK_ANCESTORS)) {
        importRoots.add(locationAsText(protoFile.getProject()));
        importRoots.add(locationAsText(protoFile.getParent()));
        return;
      }
      importRoots.add(singleImportRoot(protoFile));
      return;
    }
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

  private String locationAsText(IResource resource) {
    return resource.getLocation().toOSString();
  }

  private String singleImportRoot(IFile protoFile) {
    return singleImportRoot(locationAsFile(protoFile.getProject()), locationAsFile(protoFile));
  }

  // TODO(alruiz): Remove usage of java.io.File.
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

  private File locationAsFile(IResource resource) {
    IPath location = resource.getLocation();
    return location.toFile();
  }

  private void appendToCommand(ProtocCommand command, String importRoot) {
    command.appendOption("proto_path", importRoot);
  }
}
