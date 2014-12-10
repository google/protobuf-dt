/*
 * Copyright (c) 2014 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.scoping;

import com.google.eclipse.protobuf.model.util.Protobufs;
import com.google.eclipse.protobuf.model.util.Resources;
import com.google.eclipse.protobuf.protobuf.Import;
import com.google.eclipse.protobuf.protobuf.Protobuf;
import com.google.inject.Inject;
import com.google.inject.Provider;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.xtext.EcoreUtil2;
import org.eclipse.xtext.scoping.impl.ImportUriGlobalScopeProvider;
import org.eclipse.xtext.util.IAcceptor;
import org.eclipse.xtext.util.IResourceScopeCache;

import java.util.Iterator;
import java.util.LinkedHashSet;

/**
 * A global scope provider that reads each {@link Import} in the protobuf file, resolves it, and
 * caches the result.
 */
public class ProtobufGlobalScopeProvider extends ImportUriGlobalScopeProvider {
  @Inject
  private IResourceScopeCache cache;

  @Inject
  private Resources resources;

  @Inject
  private Protobufs protobufs;

  @Inject
  private IImportResolver importResolver;

  @Override
  protected LinkedHashSet<URI> getImportedUris(final Resource resource) {
    return cache.get(ProtobufGlobalScopeProvider.class.getName(), resource,
        new Provider<LinkedHashSet<URI>>() {
          @Override
          public LinkedHashSet<URI> get() {
            final LinkedHashSet<URI> uniqueImportURIs = new LinkedHashSet<>(5);
            IAcceptor<String> collector = createURICollector(resource, uniqueImportURIs);
            Protobuf protobuf = resources.rootOf(resource);
            if (protobuf == null) {
              return uniqueImportURIs;
            }
            for (Import anImport : protobufs.importsIn(protobuf)) {
              collector.accept(importResolver.resolve(anImport));
            }
            Iterator<URI> uriIter = uniqueImportURIs.iterator();
            while (uriIter.hasNext()) {
              if (!EcoreUtil2.isValidUri(resource, uriIter.next())) {
                uriIter.remove();
              }
            }
            return uniqueImportURIs;
          }
        });
  }
}
