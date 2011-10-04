/*
 * Copyright (c) 2011 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.scoping;

import static org.eclipse.xtext.EcoreUtil2.getAllContentsOfType;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.emf.ecore.EObject;
import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;

import com.google.eclipse.protobuf.protobuf.*;

/**
 * @author alruiz@google.com (Alex Ruiz)
 */
class ContainAllPropertiesInMessage extends BaseMatcher<IEObjectDescriptions> {

  private final Message message;

  static ContainAllPropertiesInMessage containAllPropertiesIn(Message message) {
    return new ContainAllPropertiesInMessage(message);
  }
  
  private ContainAllPropertiesInMessage(Message message) {
    this.message = message;
  }
  
  public boolean matches(Object arg) {
    if (!(arg instanceof IEObjectDescriptions)) return false;
    IEObjectDescriptions descriptions = (IEObjectDescriptions) arg;
    List<Property> properties = allProperties();
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
    for (Property property : allProperties()) {
      names.add(property.getName());
    }
    description.appendValue(names);
  }

  private List<Property> allProperties() {
    return getAllContentsOfType(message, Property.class);
  }
}
