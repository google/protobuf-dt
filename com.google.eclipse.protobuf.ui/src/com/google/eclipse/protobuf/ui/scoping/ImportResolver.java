/*
 * Copyright (c) 2014 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.ui.scoping;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.eclipse.protobuf.model.util.Imports;
import com.google.eclipse.protobuf.protobuf.Import;
import com.google.eclipse.protobuf.scoping.IImportResolver;
import com.google.eclipse.protobuf.scoping.IUriResolver;
import com.google.eclipse.protobuf.scoping.ProtoDescriptorProvider;
import com.google.eclipse.protobuf.util.EResources;
import com.google.eclipse.protobuf.util.Uris;
import com.google.inject.Inject;

import org.eclipse.core.resources.IProject;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;

import java.util.concurrent.ExecutionException;

/**
 * Resolves "import" URIs.
 *
 * @author alruiz@google.com (Alex Ruiz)
 */
public class ImportResolver implements IImportResolver {
  @Inject private ProtoDescriptorProvider descriptorProvider;
  @Inject private Imports imports;
  @Inject private IUriResolver resolver;
  @Inject private Uris uris;

  private LoadingCache<Import, String> cache =
      CacheBuilder.newBuilder().build(new CacheLoader<Import, String>() {
        @Override
        public String load(Import key) throws Exception {
          String result = internalResolveUri(key);
          if (result == null) {
            throw new Exception("Unable to resolve import: " + imports.getPath(key));
          }
          return result;
        }
      });

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
  @Override public String resolve(Import anImport) {
    try {
      return cache.get(anImport);
    } catch (ExecutionException e) {
      return null;
    }
  }

  private String internalResolveUri(Import anImport) {
    return resolveUri(imports.getPath(anImport), anImport.eResource());
  }

  private String resolveUri(String importUri, Resource resource) {
    URI location =
        descriptorProvider.descriptorLocation(EResources.getProjectOf(resource), importUri);
    if (location != null) {
      return location.toString();
    }

    URI resourceUri = resource.getURI();
    IProject project = uris.projectOfReferredFile(resourceUri);
    return resolver.resolveUri(importUri, resourceUri, project);
  }

  @Override
  public void invalidateCacheFor(Import anImport) {
    cache.invalidate(anImport);
  }
}
