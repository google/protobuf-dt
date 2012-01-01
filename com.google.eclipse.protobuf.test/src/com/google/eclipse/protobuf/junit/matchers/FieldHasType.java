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
public class FieldHasType extends BaseMatcher<MessageField> {
  private final String typeName;

  public static FieldHasType isBool() {
    return hasType("bool");
  }

  public static FieldHasType isString() {
    return hasType("string");
  }

  public static FieldHasType hasType(String typeName) {
    return new FieldHasType(typeName);
  }

  private FieldHasType(String typeName) {
    this.typeName = typeName;
  }

  @Override public boolean matches(Object arg) {
    if (!(arg instanceof MessageField)) {
      return false;
    }
    MessageField field = (MessageField) arg;
    return typeName.equals(typeNameOf(field));
  }

  private String typeNameOf(MessageField field) {
    TypeLink link = field.getType();
    if (link instanceof ScalarTypeLink) {
      return ((ScalarTypeLink) link).getTarget().getName();
    }
    if (link instanceof ComplexTypeLink) {
      ComplexType type = ((ComplexTypeLink) link).getTarget();
      return type == null ? null : type.getName();
    }
    return link.toString();
  }

  @Override public void describeTo(Description description) {
    description.appendValue(typeName);
  }
}
