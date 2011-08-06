/*
 * Copyright (c) 2011 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.ui.scoping;

import static org.eclipse.xtext.util.Strings.isEmpty;

import org.eclipse.core.resources.IProject;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;

import com.google.eclipse.protobuf.scoping.IFileUriResolver;
import com.google.eclipse.protobuf.scoping.ProtoDescriptorProvider;
import com.google.eclipse.protobuf.ui.preferences.pages.paths.PathsPreferences;
import com.google.eclipse.protobuf.ui.preferences.pages.paths.PathsPreferencesFactory;
import com.google.eclipse.protobuf.ui.util.Resources;
import com.google.eclipse.protobuf.util.Imports;
import com.google.inject.Inject;

/**
 * Resolves "import" URIs.
 *
 * @author alruiz@google.com (Alex Ruiz)
 */
public class FileUriResolver implements IFileUriResolver {

  @Inject private PathsPreferencesFactory preferencesFactory;
  @Inject private FileResolverStrategies resolvers;
  @Inject private Imports imports;
  @Inject private ProtoDescriptorProvider descriptorProvider;
  @Inject private Resources resources;

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
  public String resolveUri(String importUri, Resource declaringResource) {
    if (hasScheme(importUri)) return importUri;
    String resolved = resolveUri(importUri, declaringResource.getURI());
    return (resolved == null) ? importUri : resolved;
  }

  private boolean hasScheme(String importUri) {
    String scheme = URI.createURI(importUri).scheme();
    return !isEmpty(scheme);
  }

  private String resolveUri(String importUri, URI resourceUri) {
    if (imports.isUnresolvedDescriptorUri(importUri)) {
      return descriptorProvider.descriptorLocation().toString();
    }
    IProject project = resources.project(resourceUri);
    if (project == null) project = resources.activeProject();
    if (project == null) throw new IllegalStateException("Unable to find current project");
    PathsPreferences preferences = preferencesFactory.preferences(project);
    return resolver(preferences).resolveUri(importUri, resourceUri, preferences);
  }

  private FileResolverStrategy resolver(PathsPreferences preferences) {
    return resolvers.strategyFor(preferences.pathResolutionType());
  }
}
