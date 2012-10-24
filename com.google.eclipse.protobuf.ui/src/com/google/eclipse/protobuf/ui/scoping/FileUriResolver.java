/*
 * Copyright (c) 2011 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.ui.scoping;

import static java.util.Collections.singletonList;
import static java.util.Collections.unmodifiableList;

import static com.google.common.collect.Lists.newArrayList;
import static com.google.eclipse.protobuf.util.Workspaces.workspaceRoot;

import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.xtext.ui.XtextProjectHelper;
import org.eclipse.xtext.ui.editor.preferences.IPreferenceStoreAccess;

import com.google.eclipse.protobuf.model.util.Imports;
import com.google.eclipse.protobuf.protobuf.Import;
import com.google.eclipse.protobuf.scoping.IFileUriResolver;
import com.google.eclipse.protobuf.scoping.ProtoDescriptorProvider;
import com.google.eclipse.protobuf.ui.preferences.paths.PathsPreferences;
import com.google.eclipse.protobuf.util.Uris;
import com.google.inject.Inject;

/**
 * Resolves "import" URIs.
 *
 * @author alruiz@google.com (Alex Ruiz)
 */
public class FileUriResolver implements IFileUriResolver {
  @Inject private ProtoDescriptorProvider descriptorProvider;
  @Inject private Imports imports;
  @Inject private MultipleDirectoriesFileResolverStrategy multipleDirectories;
  @Inject private Uris uris;
  @Inject private SingleDirectoryFileResolverStrategy singleDirectory;
  @Inject private IPreferenceStoreAccess storeAccess;

  /*
   * The import URI is relative to the file where the import is. Protoc works fine, but the editor doesn't.
   * In order for the editor to see the import, we need to add to the import URI "platform:resource" and the parent
   * folder of the file containing the import.
   *
   * For example: given the following file hierarchy:
   *
   * - protobuf-test (project)
   *   - folder
   *     - proto2.proto
   *   - proto1.proto
   *
   * If we import "folder/proto2.proto" into proto1.proto, proto1.proto will compile fine, but the editor will complain.
   * We need to have the import URI as "platform:/resource/protobuf-test/folder/proto2.proto" for the editor to see it.
   */
  @Override public void resolveAndUpdateUri(Import anImport) {
    if (imports.isResolved(anImport)) {
      return;
    }
    String resolved = resolveUri(anImport.getImportURI(), anImport.eResource());
    if (resolved != null) {
      anImport.setImportURI(resolved);
    }
  }

  private String resolveUri(String importUri, Resource resource) {
    URI location = descriptorProvider.descriptorLocation(importUri);
    if (location != null) {
      return location.toString();
    }
    URI resourceUri = resource.getURI();
    IProject project = uris.projectOfReferredFile(resourceUri);
    FileResolverStrategy resolver = multipleDirectories;
    if (project == null) {
      return resolver.resolveUri(importUri, resourceUri, preferencesFromAllProjects());
    }
    PathsPreferences preferences = new PathsPreferences(storeAccess, project);
    if (!preferences.areFilesInMultipleDirectories()) {
      resolver = singleDirectory;
    }
    return resolver.resolveUri(importUri, resourceUri, singletonList(preferences));
  }

  private Iterable<PathsPreferences> preferencesFromAllProjects() {
    List<PathsPreferences> allPreferences = newArrayList();
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
