/*
 * Copyright (c) 2012 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.resource;

import static com.google.common.base.Objects.equal;
import static com.google.common.collect.Lists.newArrayList;
import static java.util.Collections.unmodifiableList;

import java.util.List;
import java.util.regex.*;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.*;
import org.eclipse.xtext.naming.*;
import org.eclipse.xtext.resource.*;

import com.google.inject.*;

/**
 * Utility methods related to <code>{@link IResourceDescription}</code>s.
 *
 * @author alruiz@google.com (Alex Ruiz)
 */
@Singleton public class ResourceDescriptions {
  @Inject private IQualifiedNameConverter converter;

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
   * Finds the URIs of the model objects, in the given resource, that:
   * <ol>
   * <li>have a qualified name that match the given pattern, and</li>
   * <li>have an {@code EClass} whose name is equal to the simple name of the given type.
   * </ol>
   * @param resource the given resource.
   * @param pattern the pattern to match.
   * @param type the type of model object we are looking for.
   * @return the URI of the matching object models, or an empty list if matches could not be found.
   */
  public List<IEObjectDescription> matchingQualifiedNames(IResourceDescription resource, Pattern pattern,
      Class<? extends EObject> type) {
    List<IEObjectDescription> descriptions = newArrayList();
    for (IEObjectDescription exported : resource.getExportedObjects()) {
      QualifiedName qualifiedName = exported.getQualifiedName();
      Matcher matcher = pattern.matcher(converter.toString(qualifiedName));
      if (matcher.matches() && haveMatchingNames(exported.getEClass(), type)) {
        descriptions.add(exported);
      }
    }
    return unmodifiableList(descriptions);
  }

  private boolean haveMatchingNames(EClass eClass, Class<? extends EObject> type) {
    return equal(eClass.getName(), type.getSimpleName());
  }
}
