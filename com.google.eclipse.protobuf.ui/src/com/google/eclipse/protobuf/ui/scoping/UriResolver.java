/*
 * Copyright (c) 2014 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.ui.scoping;

import static com.google.eclipse.protobuf.util.Workspaces.workspaceRoot;
import static java.util.Collections.unmodifiableList;

import com.google.common.collect.ImmutableList;
import com.google.eclipse.protobuf.scoping.IUriResolver;
import com.google.eclipse.protobuf.ui.preferences.paths.PathsPreferences;
import com.google.inject.Inject;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.emf.common.util.URI;
import org.eclipse.xtext.ui.XtextProjectHelper;
import org.eclipse.xtext.ui.editor.preferences.IPreferenceStoreAccess;

import java.util.ArrayList;
import java.util.List;

/**
 * Resolves URIs.
 */
public class UriResolver implements IUriResolver {
  @Inject private MultipleDirectoriesUriResolver multipleDirectories;
  @Inject private SingleDirectoryUriResolver singleDirectory;
  @Inject private IPreferenceStoreAccess storeAccess;

  @Override
  public String resolveUri(String importUri, URI declaringResourceUri, IProject project) {
    return resolveUriInternal(importUri, declaringResourceUri, project);
  }

  private String resolveUriInternal(String importUri, URI declaringResourceUri, IProject project) {
    if (project == null) {
      return multipleDirectories.resolveUri(importUri, preferencesFromAllProjects());
    }
    PathsPreferences locations = new PathsPreferences(storeAccess, project);
    if (locations.areFilesInMultipleDirectories()) {
      return multipleDirectories.resolveUri(importUri, ImmutableList.of(locations));
    }
    return singleDirectory.resolveUri(importUri, declaringResourceUri);
  }

  private Iterable<PathsPreferences> preferencesFromAllProjects() {
    List<PathsPreferences> allPreferences = new ArrayList<>();
    IWorkspaceRoot root = workspaceRoot();
    for (IProject project : root.getProjects()) {
      if (project.isHidden() || !project.isAccessible() || !XtextProjectHelper.hasNature(project)) {
        continue;
      }
      PathsPreferences preferences = new PathsPreferences(storeAccess, project);
      allPreferences.add(preferences);
    }
    return unmodifiableList(allPreferences);
  }
}
