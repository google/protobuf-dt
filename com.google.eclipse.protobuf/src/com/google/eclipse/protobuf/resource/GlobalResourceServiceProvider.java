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

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.xtext.resource.IGlobalServiceProvider.ResourceServiceProviderImpl;
import org.eclipse.xtext.resource.IResourceServiceProvider;
import org.eclipse.xtext.resource.IResourceServiceProvider.Registry;

import com.google.inject.Inject;

/**
 * @author alruiz@google.com (Alex Ruiz)
 */
public class GlobalResourceServiceProvider extends ResourceServiceProviderImpl {
  @Inject public GlobalResourceServiceProvider(Registry registry, IResourceServiceProvider provider) {
    super(registry, provider);
  }

  @Override public <T> T findService(EObject e, Class<T> serviceType) {
    if (e.eIsProxy()) {
      InternalEObject internalEObject = (InternalEObject) e;
      return findService(internalEObject.eProxyURI(), serviceType);
    }
    Resource resource = e.eResource();
    return (resource != null) ? findService(resource.getURI(), serviceType) : null;
  }
}
