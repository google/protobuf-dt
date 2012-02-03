/*
 * Copyright (c) 2012 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.cdt.path;

import static com.google.common.collect.Lists.newArrayList;
import static com.google.eclipse.protobuf.ui.preferences.compiler.core.CompilerPreferences.compilerPreferences;
import static com.google.eclipse.protobuf.ui.util.Paths.segmentsOf;
import static org.eclipse.core.runtime.IPath.SEPARATOR;
import static org.eclipse.xtext.util.Strings.*;

import java.util.List;

import org.eclipse.core.resources.*;
import org.eclipse.core.runtime.*;
import org.eclipse.xtext.ui.editor.preferences.IPreferenceStoreAccess;

import com.google.eclipse.protobuf.ui.preferences.compiler.core.CompilerPreferences;
import com.google.inject.Inject;

/**
 * Finds the path of a .proto file, given the path of the generated C++ header file.
 *
 * @author alruiz@google.com (Alex Ruiz)
 */
public class ProtoFilePathFinder {
  private static final String PATH_SEPARATOR = new String(new char[] { SEPARATOR });

  @Inject private IPreferenceStoreAccess storeAccess;

  /**
   * Returns the path of the .proto file used as source of the generated C++ header file.
   * @param file the given file.
   * @return the path of the .proto file used as source of the generated C++ header file, or {@code null} if the given
   * file is not a C++ header file or if C++ code generation is not enabled in the proto editor.
   */
  public IPath findProtoFilePath(IFile file) {
    IPath headerFilePath = file.getFullPath();
    if (!"h".equals(headerFilePath.getFileExtension())) {
      return null;
    }
    IPath cppOutputDirectory = cppOutputDirectory(file.getProject());
    if (cppOutputDirectory == null) {
      return null;
    }
    List<String> newPathSegments = newArrayList(headerFilePath.segments());
    for (int i = 0; i < headerFilePath.segmentCount() - 1; i++) {
      newPathSegments.remove(0);
      if (headerFilePath.segment(i).equals(cppOutputDirectory.lastSegment())) {
        break;
      }
    }
    int fileNameIndex = newPathSegments.size() - 1;
    String fileName = newPathSegments.get(fileNameIndex);
    newPathSegments.set(fileNameIndex, fileName.replace("pb.h", "proto"));
    return new Path(concat(PATH_SEPARATOR, newPathSegments));
  }

  private IPath cppOutputDirectory(IProject project) {
    CompilerPreferences preferences = compilerPreferences(storeAccess, project);
    if (!preferences.compileProtoFiles().getValue() && !preferences.cppCodeGenerationEnabled().getValue()) {
      return null;
    }
    String directoryName = preferences.cppOutputDirectory().getValue();
    if (isEmpty(directoryName)) {
      return null;
    }
    IFolder directory = null;
    String[] segments = segmentsOf(directoryName);
    StringBuilder pathBuilder = new StringBuilder();
    for (String segment : segments) {
      pathBuilder.append(segment);
      directory = project.getFolder(pathBuilder.toString());
      if (!directory.exists()) {
        return null;
      }
      pathBuilder.append(SEPARATOR);
    }
    if (directory == null) {
      return null;
    }
    return directory.getFullPath();
  }
}
