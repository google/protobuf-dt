/*
 * Copyright (c) 2011 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.ui.scoping;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.emf.common.util.URI;

import com.google.eclipse.protobuf.ui.preferences.paths.DirectoryPath;
import com.google.eclipse.protobuf.ui.preferences.paths.PathsPreferences;

/**
 * @author alruiz@google.com (Alex Ruiz)
 */
class MultipleDirectoriesFileResolver implements FileResolverStrategy {

  /** {@inheritDoc} */
  public String resolveUri(String importUri, URI declaringResourceUri, PathsPreferences preferences, IProject project) {
    for (DirectoryPath directoryPath : preferences.directoryPaths()) {
      String resolved = resolveUri(importUri, directoryPath.value(), project);
      if (resolved != null) return resolved;
    }
    return null;
  }

  private String resolveUri(String importUri, String directoryName, IProject project) {
    String path = directoryName + SEPARATOR + importUri;
    IResource findMember = project.findMember(path);
    boolean exists = (findMember != null) ? findMember.exists() : false;
    return (exists) ? PREFIX +  project.getFullPath() + SEPARATOR + path : null;
  }
}
