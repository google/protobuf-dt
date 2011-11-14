/*
 * Copyright (c) 2011 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.model.util;

import static com.google.eclipse.protobuf.grammar.CommonKeyword.*;
import static com.google.eclipse.protobuf.protobuf.Modifier.OPTIONAL;

import com.google.eclipse.protobuf.grammar.CommonKeyword;
import com.google.eclipse.protobuf.protobuf.*;
import com.google.inject.Singleton;

/**
 * Utility methods related to <code>{@link Property}</code>.
 *
 * @author alruiz@google.com (Alex Ruiz)
 */
@Singleton
public class Properties {

  /**
   * Indicates whether the modifier of the given property is <code>{@link Modifier#OPTIONAL}</code>.
   * @param property the given property.
   * @return {@code true} if the modifier of the given property is "optional," {@code false} otherwise.
   */
  public boolean isOptional(Property property) {
    return OPTIONAL.equals(property.getModifier());
  }

  /**
   * Indicates whether the type of the given property is primitive. Primitive types include: {@code double},
   * {@code float}, {@code int32}, {@code int64}, {@code uint32}, {@code uint64}, {@code sint32}, {@code sint64},
   * {@code fixed32}, {@code fixed64}, {@code sfixed32}, {@code sfixed64} and {@code bool}.
   * @param p the given property.
   * @return {@code true} if the type of the given property is primitive, {@code false} otherwise.
   */
  public boolean isPrimitive(Property p) {
    AbstractTypeRef r = p.getType();
    if (!(r instanceof ScalarTypeRef)) return false;
    String typeName = ((ScalarTypeRef) r).getScalar().getName();
    return !STRING.hasValue(typeName) && !BYTES.hasValue(typeName);
  }

  /**
   * Indicates whether the given property is of type {@code bool}.
   * @param p the given property.
   * @return {@code true} if the given property is of type {@code bool}, {@code false} otherwise.
   */
  public boolean isBool(Property p) {
    return isScalarType(p, BOOL);
  }

  /**
   * Indicates whether the given property can accept "nan" as its default value.
   * @param p the given property.
   * @return {@code true} if the given property  can accept "nan" as its default value, {@code false} otherwise.
   */
  public boolean mayBeNan(Property p) {
    String typeName = typeNameOf(p);
    return FLOAT.hasValue(typeName) || DOUBLE.hasValue(typeName);
  }

  /**
   * Indicates whether the given property is of type {@code string}.
   * @param p the given property.
   * @return {@code true} if the given property is of type {@code string}, {@code false} otherwise.
   */
  public boolean isString(Property p) {
    return isScalarType(p, STRING);
  }

  private boolean isScalarType(Property p, CommonKeyword typeKeyword) {
    return typeKeyword.hasValue(typeNameOf(p));
  }

  /**
   * Returns the name of the type of the given <code>{@link Property}</code>.
   * @param p the given {@code Property}.
   * @return the name of the type of the given {@code Property}.
   */
  public String typeNameOf(Property p) {
    AbstractTypeRef r = p.getType();
    if (r instanceof ScalarTypeRef) return ((ScalarTypeRef) r).getScalar().getName();
    if (r instanceof ComplexTypeLink) {
      ComplexType type = ((ComplexTypeLink) r).getTarget();
      return type == null ? null : type.getName();
    }
    return r.toString();
  }
}
