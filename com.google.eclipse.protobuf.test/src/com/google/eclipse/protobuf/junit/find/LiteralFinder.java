/*
 * Copyright (c) 2011 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.junit.find;

import static org.eclipse.xtext.EcoreUtil2.getAllContentsOfType;

import java.util.List;

import com.google.eclipse.protobuf.protobuf.Literal;

/**
 * @author alruiz@google.com (Alex Ruiz)
 */
public final class LiteralFinder {

  public static Literal findLiteral(Name name, Root root) {
    List<Literal> literals = getAllContentsOfType(root.value, Literal.class);
    for (Literal literal : literals)
      if (name.value.equals(literal.getName())) return literal;
    return null;
  }

  private LiteralFinder() {}
}
