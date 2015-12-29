/*
 * Copyright (c) 2011 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.junit;

import static java.util.Collections.unmodifiableSet;

import static com.google.common.collect.Maps.newLinkedHashMap;

import java.util.Collection;
import java.util.Map;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.xtext.naming.QualifiedName;
import org.eclipse.xtext.resource.IEObjectDescription;
import org.eclipse.xtext.scoping.IScope;

/**
 * @author alruiz@google.com (Alex Ruiz)
 */
public class IEObjectDescriptions {
  public static IEObjectDescriptions descriptionsIn(IScope scope) {
    return descriptions(scope.getAllElements());
  }

  public static IEObjectDescriptions descriptions(Iterable<IEObjectDescription> elements) {
    return new IEObjectDescriptions(elements);
  }

  private final Map<String, IEObjectDescription> descriptions = newLinkedHashMap();

  private IEObjectDescriptions(Iterable<IEObjectDescription> elements) {
    for (IEObjectDescription d : elements) {
      QualifiedName name = d.getName();
      descriptions.put(name.toString(), d);
    }
  }

  public EObject objectDescribedAs(String name) {
    IEObjectDescription d = descriptions.get(name);
    return d.getEObjectOrProxy();
  }

  public int size() {
    return descriptions.size();
  }

  public Collection<String> names() {
    return unmodifiableSet(descriptions.keySet());
  }

  @Override public String toString() {
    return descriptions.keySet().toString();
  }
}
