/*
 * Copyright (c) 2012 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.cdt.actions;

import static com.google.common.collect.Lists.newArrayList;
import static com.google.eclipse.protobuf.ui.preferences.compiler.core.CompilerPreferences.compilerPreferences;
import static org.eclipse.core.runtime.IPath.SEPARATOR;
import static org.eclipse.xtext.util.Strings.*;

import java.util.List;

import org.eclipse.core.resources.*;
import org.eclipse.core.runtime.*;
import org.eclipse.ui.IEditorPart;
import org.eclipse.xtext.ui.editor.preferences.IPreferenceStoreAccess;

import com.google.eclipse.protobuf.ui.preferences.compiler.core.CompilerPreferences;
import com.google.inject.Inject;

/**
 * @author alruiz@google.com (Alex Ruiz)
 */
class ProtoFilePathFinder {
  private static final String PATH_SEPARATOR = new String(new char[] { SEPARATOR });

  @Inject private IPreferenceStoreAccess storeAccess;

  IPath findProtoFilePath(IEditorPart editor) {
    IFile file = (IFile) editor.getEditorInput().getAdapter(IFile.class);
    IPath headerFilePath = file.getFullPath();
    if (!"h".equals(headerFilePath.getFileExtension())) {
      return null;
    }
    String cppOutputDirectory = cppOutputDirectory(file.getProject());
    if (isEmpty(cppOutputDirectory)) {
      return null;
    }
    List<String> segments = newArrayList(headerFilePath.segments());
    for (int i = 0; i < headerFilePath.segmentCount() - 1; i++) {
      segments.remove(0);
      if (headerFilePath.segment(i).equals(cppOutputDirectory)) {
        break;
      }
    }
    String headerFileName = headerFilePath.lastSegment();
    segments.set(segments.size() - 1, headerFileName.replace("pb.h", "proto"));
    String protoFilePath = concat(PATH_SEPARATOR, segments);
    return new Path(protoFilePath);
  }

  private String cppOutputDirectory(IProject project) {
    CompilerPreferences preferences = compilerPreferences(storeAccess, project);
    if (!preferences.compileProtoFiles().getValue() && !preferences.cppCodeGenerationEnabled().getValue()) {
      return null;
    }
    return preferences.cppOutputDirectory().getValue();
  }
}
