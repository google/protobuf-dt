/*
 * Copyright (c) 2012 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.resource;

import static com.google.common.collect.Iterables.filter;
import static com.google.common.collect.Lists.newArrayList;
import static java.util.Collections.unmodifiableList;

import java.util.List;

import org.eclipse.emf.common.util.URI;
import org.eclipse.xtext.naming.QualifiedName;
import org.eclipse.xtext.resource.*;

import com.google.common.base.Predicate;
import com.google.inject.Singleton;

/**
 * Utility methods related to <code>{@link IResourceDescription}</code>s.
 *
 * @author alruiz@google.com (Alex Ruiz)
 */
@Singleton public class ResourceDescriptions {
  /**
   * Finds the URI of a model object, in the given resource, whose qualified name matches the given one.
   * @param resource the given resource.
   * @param qualifiedName the qualified name to match.
   * @return the URI of the matching model object, or {@code null} if a model object with a matching URI could not be
   * found.
   */
  public URI modelObjectUri(IResourceDescription resource, QualifiedName qualifiedName) {
    for (IEObjectDescription exported : resource.getExportedObjects()) {
      QualifiedName modelObjectQualifiedName = exported.getQualifiedName();
      if (qualifiedName.equals(modelObjectQualifiedName)) {
        return exported.getEObjectURI();
      }
    }
    return null;
  }

  /**
   * Returns the model objects that match the criteria specified in the given filter.
   * @param resource the resource containing model objects.
   * @param filter the filter to use.
   * @return  the model objects that match the criteria specified in the given filter.
   */
  public List<IEObjectDescription> filterModelObjects(IResourceDescription resource, Predicate<IEObjectDescription> filter) {
    List<IEObjectDescription> filtered = newArrayList(filter(resource.getExportedObjects(), filter));
    return unmodifiableList(filtered);
  }
}
