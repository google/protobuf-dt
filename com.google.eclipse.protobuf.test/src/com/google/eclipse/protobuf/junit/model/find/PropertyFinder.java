/*
 * Copyright (c) 2011 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.junit.model.find;

import static org.eclipse.xtext.EcoreUtil2.getAllContentsOfType;

import java.util.List;

import org.eclipse.emf.ecore.EObject;

import com.google.eclipse.protobuf.protobuf.Property;

/**
 * @author alruiz@google.com (Alex Ruiz)
 */
public final class PropertyFinder {

  public static Property findProperty(Name name, Root root) {
    for (Property property : allPropertiesIn(root.value))
      if (name.value.equals(property.getName())) return property;
    return null;
  }

  public static List<Property> allPropertiesIn(EObject root) {
    return getAllContentsOfType(root, Property.class);
  }

  private PropertyFinder() {}
}
