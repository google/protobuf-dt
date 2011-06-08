/*
 * Copyright (c) 2011 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.ui.util;

import static java.lang.Math.max;

import org.eclipse.emf.ecore.EObject;

import com.google.eclipse.protobuf.protobuf.Field;
import com.google.inject.Singleton;

/**
 * Utility methods re <code>{@link Field}</code>.
 *
 * @author alruiz@google.com (Alex Ruiz)
 */
@Singleton
public class Fields {

  /**
   * Calculates the tag number value for the given field. The calculated tag number value is the maximum of all the
   * tag number values of the given field's siblings, plus one. The minimum tag number value is 1.
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
   * The calculated tag number value for the field {@code PhoneNumber} will be 3.
   * </p>
   * @param f the given field.
   * @return the calculated value for the tag number of the given field.
   */
  public int calculateTagNumberOf(Field f) {
    int index = 0;
    for (EObject o : f.eContainer().eContents()) {
      if (o == f || !(o instanceof Field)) continue;
      index = max(index, ((Field) o).getIndex());
    }
    return ++index;
  }
}
