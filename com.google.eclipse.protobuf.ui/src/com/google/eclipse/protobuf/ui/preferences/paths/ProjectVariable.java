/*
 * Copyright (c) 2011 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.ui.preferences.paths;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.*;

/**
 * ${project} variable.
 * 
 * @author alruiz@google.com (Alex Ruiz)
 */
class ProjectVariable {

  private static final String VARIABLE_VALUE = "${project}";

  static IPath useProjectVariable(IPath path, IProject project) {
    return switchProjectSegments(path, project.getName(), VARIABLE_VALUE);
  }
  
  static String useProjectName(String path, IProject project) {
    IPath newPath = switchProjectSegments(new Path(path), VARIABLE_VALUE, project.getName());
    return newPath.toString();
  }
  
  private static IPath switchProjectSegments(IPath path, String currentSegment, String newSegment) {
    if (!currentSegment.equals(path.segment(0))) return path;
    IPath newPath = new Path(newSegment);
    newPath = newPath.append(path.removeFirstSegments(1));
    if (path.isAbsolute()) newPath = newPath.makeAbsolute();
    return newPath;
  }
}
