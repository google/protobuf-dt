/*
 * Copyright (c) 2011 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.ui.builder.nature.resourceloader;

import static com.google.common.collect.Collections2.filter;

import com.google.common.base.Predicate;
import com.google.common.collect.Lists;

import org.eclipse.core.resources.IProject;
import org.eclipse.emf.common.util.*;
import org.eclipse.emf.ecore.resource.*;
import org.eclipse.xtext.builder.resourceloader.SerialResourceLoader;
import org.eclipse.xtext.ui.resource.IResourceSetProvider;

import java.util.*;

/**
 * @author alruiz@google.com (Alex Ruiz)
 */
@SuppressWarnings("restriction")
public class ProtobufSerialResourceLoader extends SerialResourceLoader {
  public ProtobufSerialResourceLoader(IResourceSetProvider resourceSetProvider, Sorter sorter) {
    super(resourceSetProvider, sorter);
  }

  @Override public LoadOperation create(final ResourceSet parent, IProject project) {
    final Queue<URI> queue = Lists.newLinkedList();
    return new CheckedLoadOperation(new LoadOperation() {
      @Override public LoadResult next() {
        URI uri = queue.poll();
        try {
          Resource resource = parent.getResource(uri, true);
          return new LoadResult(resource, uri);
        } catch (WrappedException e) {
          throw new LoadOperationException(uri, e.getCause());
        }
      }

      @Override public boolean hasNext() {
        return !queue.isEmpty();
      }

      @Override public Collection<URI> cancel() {
        return queue;
      }

      @Override public void load(Collection<URI> uris) {
        Collection<URI> filtered = filter(uris, new Predicate<URI>() {
          @Override public boolean apply(URI input) {
            return !input.toString().endsWith("BulkMutatePayloadPseudoService.proto");
          }
        });
        queue.addAll(getSorter().sort(filtered));
      }
    });
  }

}
