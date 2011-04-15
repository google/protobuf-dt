/*
 * Copyright (c) 2011 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.scoping;

import static com.google.common.collect.Iterables.*;
import static org.eclipse.xtext.EcoreUtil2.getResource;

import java.util.Collection;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.xtext.resource.*;
import org.eclipse.xtext.resource.IResourceDescription.Manager;
import org.eclipse.xtext.scoping.impl.LoadOnDemandResourceDescriptions;

import com.google.common.base.Function;
import com.google.common.base.Predicates;
import com.google.inject.Inject;

/**
 * Similar to <code>{@link LoadOnDemandResourceDescriptions}</code> but it does not throw exceptions if a
 * <code>{@link IResourceDescription}</code> cannot be obtained from a <code>{@link URI}</code>.
 *
 * @author alruiz@google.com (Alex Ruiz)
 */
public class ResourceDescriptions extends LoadOnDemandResourceDescriptions {

  private IResourceDescriptions delegate;
  private Collection<URI> validUris;
  private Resource context;

  @Inject private IResourceServiceProvider.Registry serviceProviderRegistry;

  @Override
  public void initialize(IResourceDescriptions newDelegate, Collection<URI> newValidUris, Resource newContext) {
    delegate = newDelegate;
    validUris = newValidUris;
    context = newContext;
  }

  @Override public Iterable<IResourceDescription> getAllResourceDescriptions() {
    return filter(transform(validUris, new Function<URI, IResourceDescription>() {
      public IResourceDescription apply(URI from) {
        return getResourceDescription(from);
      }
    }), Predicates.notNull());
  }

  @Override public boolean isEmpty() {
    return validUris.isEmpty();
  }

  @Override protected Iterable<? extends ISelectable> getSelectables() {
    return getAllResourceDescriptions();
  }

  @Override public IResourceDescription getResourceDescription(URI uri) {
    IResourceDescription result = delegate.getResourceDescription(uri);
    if (result == null) return result;
    Resource resource = getResource(context, uri.toString());
    if (resource == null) return null;
    IResourceServiceProvider provider = serviceProviderRegistry.getResourceServiceProvider(uri);
    if (provider == null) return null;
    Manager manager = provider.getResourceDescriptionManager();
    if (manager == null) return null;
    return manager.getResourceDescription(resource);
  }
}
