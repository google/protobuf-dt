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

import com.google.eclipse.protobuf.protobuf.Literal;
import com.google.inject.Singleton;

/**
 * Utility methods for instances of <code>{@link Literal}</code>.
 *
 * @author alruiz@google.com (Alex Ruiz)
 */
@Singleton
public class Literals {

  public int calculateIndexOf(Literal l) {
    int index = 0;
    for (EObject o : l.eContainer().eContents()) {
      if (o == l || !(o instanceof Literal)) continue;
      Literal c = (Literal) o;
      index = max(index, c.getIndex());
    }
    return ++index;
  }
}
