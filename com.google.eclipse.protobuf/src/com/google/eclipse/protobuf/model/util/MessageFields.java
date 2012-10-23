/*
 * Copyright (c) 2011 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.model.util;

import static com.google.eclipse.protobuf.grammar.CommonKeyword.BOOL;
import static com.google.eclipse.protobuf.grammar.CommonKeyword.BYTES;
import static com.google.eclipse.protobuf.grammar.CommonKeyword.DOUBLE;
import static com.google.eclipse.protobuf.grammar.CommonKeyword.FIXED32;
import static com.google.eclipse.protobuf.grammar.CommonKeyword.FIXED64;
import static com.google.eclipse.protobuf.grammar.CommonKeyword.FLOAT;
import static com.google.eclipse.protobuf.grammar.CommonKeyword.INT32;
import static com.google.eclipse.protobuf.grammar.CommonKeyword.INT64;
import static com.google.eclipse.protobuf.grammar.CommonKeyword.SFIXED32;
import static com.google.eclipse.protobuf.grammar.CommonKeyword.SFIXED64;
import static com.google.eclipse.protobuf.grammar.CommonKeyword.SINT32;
import static com.google.eclipse.protobuf.grammar.CommonKeyword.SINT64;
import static com.google.eclipse.protobuf.grammar.CommonKeyword.STRING;
import static com.google.eclipse.protobuf.grammar.CommonKeyword.UINT32;
import static com.google.eclipse.protobuf.grammar.CommonKeyword.UINT64;
import static com.google.eclipse.protobuf.protobuf.Modifier.OPTIONAL;

import com.google.eclipse.protobuf.grammar.CommonKeyword;
import com.google.eclipse.protobuf.protobuf.ComplexType;
import com.google.eclipse.protobuf.protobuf.ComplexTypeLink;
import com.google.eclipse.protobuf.protobuf.Enum;
import com.google.eclipse.protobuf.protobuf.Message;
import com.google.eclipse.protobuf.protobuf.MessageField;
import com.google.eclipse.protobuf.protobuf.Modifier;
import com.google.eclipse.protobuf.protobuf.ScalarType;
import com.google.eclipse.protobuf.protobuf.ScalarTypeLink;
import com.google.eclipse.protobuf.protobuf.TypeLink;
import com.google.inject.Singleton;

/**
 * Utility methods related to <code>{@link MessageField}</code>s.
 *
 * @author alruiz@google.com (Alex Ruiz)
 */
@Singleton public class MessageFields {
  /**
   * Indicates whether the modifier of the given field is <code>{@link Modifier#OPTIONAL}</code>.
   * @param field the given field.
   * @return {@code true} if the modifier of the given field is "optional," {@code false} otherwise.
   */
  public boolean isOptional(MessageField field) {
    return OPTIONAL.equals(field.getModifier());
  }

  /**
   * Indicates whether the type of the given field is primitive. Primitive types include: {@code double}, {@code float},
   * {@code int32}, {@code int64}, {@code uint32}, {@code uint64}, {@code sint32}, {@code sint64}, {@code fixed32},
   * {@code fixed64}, {@code sfixed32}, {@code sfixed64} and {@code bool}.
   * @param field the given field.
   * @return {@code true} if the type of the given field is primitive, {@code false} otherwise.
   */
  public boolean isPrimitive(MessageField field) {
    TypeLink link = field.getType();
    if (!(link instanceof ScalarTypeLink)) {
      return false;
    }
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
   * Indicates whether the given field is of type {@code bytes}.
   * @param field the given field.
   * @return {@code true} if the given field is of type {@code bytes}, {@code false} otherwise.
   */
  public boolean isBytes(MessageField field) {
    return isScalarType(field, BYTES);
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

  private boolean isScalarType(MessageField field, CommonKeyword... scalarNames) {
    TypeLink link = field.getType();
    if (link instanceof ScalarTypeLink) {
      String typeName = ((ScalarTypeLink) link).getTarget().getName();
      for (CommonKeyword scalarName : scalarNames) {
        if (scalarName.hasValue(typeName)) {
          return true;
        }
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
    ScalarType scalarType = scalarTypeOf(field);
    if (scalarType != null) {
      return scalarType.getName();
    }
    ComplexType complexType = typeOf(field);
    if (complexType != null) {
      return complexType.getName();
    }
    return null;
  }

  /**
   * Returns the message type of the given field, only if the type of the given field is a message.
   * @param field the given field.
   * @return the message type of the given field or {@code null} if the type of the given field is not message.
   */
  public Message messageTypeOf(MessageField field) {
    return fieldType(field, Message.class);
  }

  /**
   * Returns the enum type of the given field, only if the type of the given field is an enum.
   * @param field the given field.
   * @return the enum type of the given field or {@code null} if the type of the given field is not enum.
   */
  public Enum enumTypeOf(MessageField field) {
    return fieldType(field, Enum.class);
  }

  private <T extends ComplexType> T fieldType(MessageField field, Class<T> targetType) {
    ComplexType type = typeOf(field);
    if (targetType.isInstance(type)) {
      return targetType.cast(type);
    }
    return null;
  }

  /**
   * Returns the type of the given field.
   * @param field the given field.
   * @return the type of the given field.
   */
  public ComplexType typeOf(MessageField field) {
    TypeLink link = field.getType();
    if (link instanceof ComplexTypeLink) {
      return ((ComplexTypeLink) link).getTarget();
    }
    return null;
  }

  /**
   * Returns the scalar type of the given field, only if the type of the given field is a scalar.
   * @param field the given field.
   * @return the scalar type of the given field or {@code null} if the type of the given field is not a scalar.
   */
  public ScalarType scalarTypeOf(MessageField field) {
    TypeLink link = (field).getType();
    if (link instanceof ScalarTypeLink) {
      return ((ScalarTypeLink) link).getTarget();
    }
    return null;
  }
}
