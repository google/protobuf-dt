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
 * Utility methods related to <code>{@link MessageField}</code>s.
 *
 * @author alruiz@google.com (Alex Ruiz)
 */
@Singleton
public class MessageFields {

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
   * Indicates whether the given field is of type {@code float} or {@code double}.
   * @param field the given field.
   * @return {@code true} if the given field is a floating point number, {@code false} otherwise.
   */
  public boolean isFloatingPointNumber(MessageField field) {
    return isScalarType(field, FLOAT, DOUBLE);
  }

  /**
   * Indicates whether the given field is of type {@code fixed32}, {@code fixed64}, {@code int32}, {@code int64},
   * {@code sfixed32}, {@code sfixed64}, {@code sint32}, {@code sint64}, {@code uint32} or {@code uint64}.
   * @param field the given field.
   * @return {@code true} if the given field is an integer, {@code false} otherwise.
   */
  public boolean isInteger(MessageField field) {
    return isScalarType(field, FIXED32, FIXED64, INT32, INT64, SFIXED32, SFIXED64, SINT32, SINT64, UINT32, UINT64);
  }

  /**
   * Indicates whether the given field is of type {@code string}.
   * @param field the given field.
   * @return {@code true} if the given field is of type {@code string}, {@code false} otherwise.
   */
  public boolean isString(MessageField field) {
    return isScalarType(field, STRING);
  }

  /**
   * Indicates whether the given field is of type {@code fixed32}, {@code fixed64}, {@code uint32} or {@code uint64}.
   * @param field the given field.
   * @return {@code true} if the given field is an unsigned integer, {@code false} otherwise.
   */
  public boolean isUnsignedInteger(MessageField field) {
    return isScalarType(field, FIXED32, FIXED64, UINT32, UINT64);
  }

  private boolean isScalarType(MessageField field, CommonKeyword...scalarNames) {
    TypeLink link = field.getType();
    if (link instanceof ScalarTypeLink) {
      String typeName = ((ScalarTypeLink) link).getTarget().getName();
      for (CommonKeyword scalarName : scalarNames) {
        if (scalarName.hasValue(typeName)) return true;
      }
    }
    return false;
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
      return (type == null) ? null : type.getName();
    }
    return link.toString();
  }
}
