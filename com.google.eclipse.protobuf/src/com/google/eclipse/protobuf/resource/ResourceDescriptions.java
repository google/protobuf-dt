/*
 * Copyright (c) 2012 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.resource;

import static com.google.common.collect.Lists.newArrayList;
import static java.util.Collections.unmodifiableList;

import java.util.List;
import java.util.regex.*;

import org.eclipse.emf.common.util.URI;
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
   * Finds the URI of a model object in the given resource whose qualified name matches the given one.
   * @param resource the given resource.
   * @param qualifiedName the qualified name to match.
   * @return the URI of the found model object, or {@code null} if a model object with a matching URI could not be
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

  public List<IEObjectDescription> matchingQualifiedNames(IResourceDescription resource, Pattern pattern) {
    List<IEObjectDescription> descriptions = newArrayList();
    for (IEObjectDescription exported : resource.getExportedObjects()) {
      QualifiedName qualifiedName = exported.getQualifiedName();
      Matcher matcher = pattern.matcher(converter.toString(qualifiedName));
      if (matcher.matches()) {
        descriptions.add(exported);
      }
    }
    return unmodifiableList(descriptions);
  }
}
