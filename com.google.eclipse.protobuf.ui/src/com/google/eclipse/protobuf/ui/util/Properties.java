/*
 * Copyright (c) 2011 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.ui.util;

import static com.google.eclipse.protobuf.ui.grammar.CommonKeyword.*;
import static java.lang.Math.max;
import static org.eclipse.xtext.EcoreUtil2.getAllContentsOfType;

import java.util.List;

import com.google.eclipse.protobuf.protobuf.*;
import com.google.eclipse.protobuf.ui.grammar.CommonKeyword;
import com.google.inject.Singleton;

/**
 * Utility methods re <code>{@link Property}</code>.
 *
 * @author alruiz@google.com (Alex Ruiz)
 */
@Singleton
public class Properties {

  /**
   * Indicates whether the type of the given property is primitive. Primitive types include: {@code double},
   * {@code float}, {@code int32}, {@code int64}, {@code uint32}, {@code uint64}, {@code sint32}, {@code sint64},
   * {@code fixed32}, {@code fixed64}, {@code sfixed32}, {@code sfixed64} and {@code bool}.
   * @param p the given property.
   * @return {@code true} if the type of the given property is primitive, {@code false} otherwise.
   */
  public boolean isPrimitive(Property p) {
    AbstractTypeReference r = p.getType();
    if (!(r instanceof ScalarTypeReference)) return false;
    String typeName = ((ScalarTypeReference) r).getScalar().getName();
    return !STRING.hasValueEqualTo(typeName) && !BYTES.hasValueEqualTo(typeName);
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
   * Indicates whether the given property is of type {@code string}.
   * @param p the given property.
   * @return {@code true} if the given property is of type {@code string}, {@code false} otherwise.
   */
  public boolean isString(Property p) {
    return isScalarType(p, STRING);
  }

  private boolean isScalarType(Property p, CommonKeyword typeKeyword) {
    return typeKeyword.hasValueEqualTo(typeNameOf(p));
  }

  /**
   * Returns the name of the type of the given <code>{@link Property}</code>.
   * @param p the given {@code Property}.
   * @return the name of the type of the given {@code Property}.
   */
  public String typeNameOf(Property p) {
    AbstractTypeReference r = p.getType();
    if (r instanceof ScalarTypeReference) return ((ScalarTypeReference) r).getScalar().getName();
    if (r instanceof TypeReference) {
      Type type = ((TypeReference) r).getType();
      return type == null ? null : type.getName();
    }
    return r.toString();
  }

  /**
   * Calculates the tag number value for the given property. The calculated tag number value is the maximum of all the
   * tag number values of the given property's siblings, plus one. The minimum tag number value is 1.
   * <p>
   * For example, in the following message:
   *
   * <pre>
   * message Person {
   *   required string name = 1;
   *   optional string email = 2;
   *   optional PhoneNumber phone =
   * </pre>
   *
   * The calculated tag number value for the property {@code PhoneNumber} will be 3.
   * </p>
   * @param p the given property.
   * @return the calculated value for the tag number of the given property.
   */
  public int calculateTagNumberOf(Property p) {
    int index = 0;
    List<Property> allProperties = getAllContentsOfType(p.eContainer(), Property.class);
    for (Property c : allProperties) {
      if (c == p) continue;
      index = max(index, c.getIndex());
    }
    return ++index;
  }
}
