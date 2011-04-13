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

import com.google.eclipse.protobuf.protobuf.*;
import com.google.eclipse.protobuf.ui.grammar.Keywords;
import com.google.inject.Inject;
import com.google.inject.Singleton;

/**
 * Utility methods for instances of <code>{@link Property}</code>.
 *
 * @author alruiz@google.com (Alex Ruiz)
 */
@Singleton
public class Properties {

  @Inject private Keywords keywords;

  public boolean isStringProperty(Property p) {
    return keywords.string().getValue().equals(nameOfTypeIn(p));
  }

  public boolean isBoolProperty(Property p) {
    return keywords.bool().getValue().equals(nameOfTypeIn(p));
  }

  /**
   * Returns the name of the type of the given <code>{@link Property}</code>.
   * @param p the given {@code Property}.
   * @return the name of the type of the given {@code Property}.
   */
  public String nameOfTypeIn(Property p) {
    AbstractTypeReference r = p.getType();
    if (r instanceof ScalarTypeReference) return ((ScalarTypeReference) r).getScalar().getName();
    if (r instanceof TypeReference) {
      Type type = ((TypeReference) r).getType();
      return type == null ? null : type.getName();
    }
    return r.toString();
  }

  public int calculateIndexOf(Property p) {
    int index = 0;
    for (EObject o : p.eContainer().eContents()) {
      if (o == p || !(o instanceof Property)) continue;
      Property c = (Property) o;
      index = max(index, c.getIndex());
    }
    return ++index;
  }
}
