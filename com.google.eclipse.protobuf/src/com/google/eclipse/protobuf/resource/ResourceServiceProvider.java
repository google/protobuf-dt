/*
 * Copyright (c) 2011 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License v1.0 which
 * accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.resource;

import org.eclipse.emf.ecore.*;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.xtext.resource.IGlobalServiceProvider.ResourceServiceProviderImpl;
import org.eclipse.xtext.resource.*;

import com.google.inject.Inject;

/**
 * @author alruiz@google.com (Alex Ruiz)
 */
public class ResourceServiceProvider extends ResourceServiceProviderImpl {
  @Inject
  public ResourceServiceProvider(IResourceServiceProvider.Registry registry, IResourceServiceProvider provider) {
    super(registry, provider);
  }

  @Override public <T> T findService(EObject e, Class<T> serviceType) {
    if (e.eIsProxy()) {
      return findService(((InternalEObject) e).eProxyURI(), serviceType);
    }
    Resource resource = e.eResource();
    if (resource == null) {
      return null;
    }
    return findService(resource.getURI(), serviceType);
  }
}
