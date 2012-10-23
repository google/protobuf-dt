/*
 * Copyright (c) 2011 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.model.util;

import static java.lang.Math.max;

import static org.eclipse.xtext.EcoreUtil2.getAllContentsOfType;

import java.util.List;

import com.google.eclipse.protobuf.protobuf.Literal;
import com.google.inject.Singleton;

/**
 * Utility methods related to <code>{@link Literal}</code>.
 *
 * @author alruiz@google.com (Alex Ruiz)
 */
@Singleton public class Literals {
  /**
   * Calculates the index value for the given literal. The calculated index value is the maximum of all the index values
   * of the given literal's siblings, plus one. The minimum index value is zero.
   * <p>
   * For example, in the following message:
   * <pre>
   * enum PhoneType {
   *   MOBILE = 0;
   *   HOME = 1;
   *   WORK =
   * </pre>
   * The calculated index value for the literal {@code WORK} will be 2.
   * </p>
   * @param literal the given literal.
   * @return the calculated index value.
   */
  public long calculateNewIndexOf(Literal literal) {
    long index = -1;
    List<Literal> allLiterals = getAllContentsOfType(literal.eContainer(), Literal.class);
    for (Literal c : allLiterals) {
      if (c == literal) {
        continue;
      }
      index = max(index, c.getIndex());
    }
    return ++index;
  }
}
