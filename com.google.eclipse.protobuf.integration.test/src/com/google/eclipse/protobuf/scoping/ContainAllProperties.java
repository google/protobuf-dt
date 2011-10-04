/*
 * Copyright (c) 2011 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.scoping;

import com.google.eclipse.protobuf.protobuf.Property;

import org.eclipse.emf.ecore.EObject;
import org.hamcrest.*;

import java.util.*;

/**
 * @author alruiz@google.com (Alex Ruiz)
 */
class ContainAllProperties extends BaseMatcher<IEObjectDescriptions> {

  private final Collection<Property> properties = new ArrayList<Property>();

  static ContainAllProperties containAll(Collection<Property> properties) {
    return new ContainAllProperties(properties);
  }
  
  private ContainAllProperties(Collection<Property> properties) {
    this.properties.addAll(properties);
  }
  
  public boolean matches(Object arg) {
    if (!(arg instanceof IEObjectDescriptions)) return false;
    IEObjectDescriptions descriptions = (IEObjectDescriptions) arg;
    if (descriptions.size() != properties.size()) return false;
    for (Property property : properties) {
      String name = property.getName();
      EObject described = descriptions.objectDescribedAs(name);
      if (described != property) return false;
    }
    return true;
  }

  public void describeTo(Description description) {
    List<String> names = new ArrayList<String>();
    for (Property property : properties) {
      names.add(property.getName());
    }
    description.appendValue(names);
  }
}
