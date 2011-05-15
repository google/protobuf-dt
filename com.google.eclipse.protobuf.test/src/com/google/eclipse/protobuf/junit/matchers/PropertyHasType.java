/*
 * Copyright (c) 2011 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.junit.matchers;

import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;

import com.google.eclipse.protobuf.protobuf.*;

/**
 * @author alruiz@google.com (Alex Ruiz)
 */
public class PropertyHasType extends BaseMatcher<Property> {

  private final String typeName;

  public static PropertyHasType hasType(String typeName) {
    return new PropertyHasType(typeName);
  }
  
  private PropertyHasType(String typeName) {
    this.typeName = typeName;
  }
  
  /** {@inheritDoc} */
  public boolean matches(Object arg) {
    if (!(arg instanceof Property)) return false;
    Property property = (Property) arg;
    return typeName.equals(typeNameOf(property));
  }
  
  private String typeNameOf(Property property) {
    AbstractTypeReference r = property.getType();
    if (r instanceof ScalarTypeReference) return ((ScalarTypeReference) r).getScalar().getName();
    if (r instanceof TypeReference) {
      Type type = ((TypeReference) r).getType();
      return type == null ? null : type.getName();
    }
    return r.toString();
  }

  /** {@inheritDoc} */
  public void describeTo(Description description) {
    description.appendValue(typeName);
  }
}
