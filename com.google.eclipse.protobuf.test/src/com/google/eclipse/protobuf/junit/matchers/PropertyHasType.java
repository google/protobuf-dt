/*
 * Copyright (c) 2011 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.junit.matchers;

import org.hamcrest.*;

import com.google.eclipse.protobuf.protobuf.*;

/**
 * @author alruiz@google.com (Alex Ruiz)
 */
public class PropertyHasType extends BaseMatcher<Property> {

  private final String typeName;

  public static PropertyHasType isBool() {
    return hasType("bool");
  }

  public static PropertyHasType isString() {
    return hasType("string");
  }

  public static PropertyHasType hasType(String typeName) {
    return new PropertyHasType(typeName);
  }

  private PropertyHasType(String typeName) {
    this.typeName = typeName;
  }

  @Override public boolean matches(Object arg) {
    if (!(arg instanceof Property)) return false;
    Property property = (Property) arg;
    return typeName.equals(typeNameOf(property));
  }

  private String typeNameOf(Property property) {
    AbstractTypeRef r = property.getType();
    if (r instanceof ScalarTypeRef) return ((ScalarTypeRef) r).getScalar().getName();
    if (r instanceof TypeRef) {
      Type type = ((TypeRef) r).getType();
      return type == null ? null : type.getName();
    }
    return r.toString();
  }

  @Override public void describeTo(Description description) {
    description.appendValue(typeName);
  }
}
