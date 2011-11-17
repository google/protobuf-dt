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
import com.google.inject.*;

/**
 * Utility methods related to <code>{@link MessageField}</code>.
 *
 * @author alruiz@google.com (Alex Ruiz)
 */
@Singleton
public class Fields {

  private @Inject Names names;

  /**
   * Indicates whether the modifier of the given field is <code>{@link Modifier#OPTIONAL}</code>.
   * @param field the given field.
   * @return {@code true} if the modifier of the given field is "optional," {@code false} otherwise.
   */
  public boolean isOptional(MessageField field) {
    return OPTIONAL.equals(field.getModifier());
  }

  /**
   * Indicates whether the type of the given field is primitive. Primitive types include: {@code double},
   * {@code float}, {@code int32}, {@code int64}, {@code uint32}, {@code uint64}, {@code sint32}, {@code sint64},
   * {@code fixed32}, {@code fixed64}, {@code sfixed32}, {@code sfixed64} and {@code bool}.
   * @param field the given field.
   * @return {@code true} if the type of the given field is primitive, {@code false} otherwise.
   */
  public boolean isPrimitive(MessageField field) {
    TypeLink link = field.getType();
    if (!(link instanceof ScalarTypeLink)) return false;
    String typeName = ((ScalarTypeLink) link).getTarget().getName();
    return !STRING.hasValue(typeName) && !BYTES.hasValue(typeName);
  }

  /**
   * Indicates whether the given field is of type {@code bool}.
   * @param field the given field.
   * @return {@code true} if the given field is of type {@code bool}, {@code false} otherwise.
   */
  public boolean isBool(MessageField field) {
    return isScalarType(field, BOOL);
  }

  /**
   * Indicates whether the given field can accept "nan" as its default value.
   * @param field the given field.
   * @return {@code true} if the given field can accept "nan" as its default value, {@code false} otherwise.
   */
  public boolean mayBeNan(MessageField field) {
    String typeName = typeNameOf(field);
    return FLOAT.hasValue(typeName) || DOUBLE.hasValue(typeName);
  }

  /**
   * Indicates whether the given field is of type {@code string}.
   * @param field the given field.
   * @return {@code true} if the given field is of type {@code string}, {@code false} otherwise.
   */
  public boolean isString(MessageField field) {
    return isScalarType(field, STRING);
  }

  private boolean isScalarType(MessageField field, CommonKeyword typeKeyword) {
    return typeKeyword.hasValue(typeNameOf(field));
  }

  /**
   * Returns the name of the type of the given field.
   * @param field the given field.
   * @return the name of the type of the given field.
   */
  public String typeNameOf(MessageField field) {
    TypeLink link = field.getType();
    if (link instanceof ScalarTypeLink) return ((ScalarTypeLink) link).getTarget().getName();
    if (link instanceof ComplexTypeLink) {
      ComplexType type = ((ComplexTypeLink) link).getTarget();
      if (type == null) return null;
      return names.valueOf(type.getName());
    }
    return link.toString();
  }
}
