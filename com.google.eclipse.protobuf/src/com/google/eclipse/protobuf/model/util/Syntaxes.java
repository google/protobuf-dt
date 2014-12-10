/*
 * Copyright (c) 2014 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.model.util;

import com.google.eclipse.protobuf.protobuf.ProtobufFactory;
import com.google.eclipse.protobuf.protobuf.StringLiteral;
import com.google.eclipse.protobuf.protobuf.Syntax;
import com.google.eclipse.protobuf.util.Strings;
import com.google.inject.Inject;
import com.google.inject.Singleton;

/**
 * @author alruiz@google.com (Alex Ruiz)
 */
@Singleton public final class Syntaxes {
  public static final String PROTO2 = "proto2";
  public static final String PROTO3 = "proto3";

  @Inject
  private StringLiterals stringLiterals;

  /**
   * Indicates whether the given {@code String} is equal to <code>{@link #PROTO2}</code>.
   * @param s the {@code String} to check.
   * @return {@code true} if the given {@code String} is equal to "proto2," {@code false} otherwise.
   */
  public boolean isSpecifyingProto2Syntax(String s) {
    return PROTO2.equals(s);
  }

  /**
   * Indicates whether the given {@code Syntax} specifies proto2 syntax.
   * @param s the {@code Syntax} to check.
   * @return {@code true} if the given {@code Syntax} specifies "proto2," {@code false} otherwise.
   */
  public boolean isSpecifyingProto2Syntax(Syntax s) {
    return PROTO2.equals(getName(s));
  }

  /**
   * Indicates whether the given {@code String} is equal to <code>{@link #PROTO3}</code>.
   * @param s the {@code String} to check.
   * @return {@code true} if the given {@code String} is equal to "proto3," {@code false} otherwise.
   */
  public boolean isSpecifyingProto3Syntax(String s) {
    return PROTO3.equals(s);
  }

  /**
   * Indicates whether the given {@code Syntax} specifies proto3 syntax.
   * @param s the {@code Syntax} to check.
   * @return {@code true} if the given {@code Syntax} specifies "proto3," {@code false} otherwise.
   */
  public boolean isSpecifyingProto3Syntax(Syntax s) {
    return PROTO3.equals(getName(s));
  }

  /**
   * Returns the name of the syntax specified by this syntax element.
   */
  public String getName(Syntax s) {
    return stringLiterals.getCombinedString(s.getName());
  }

  /**
   * Sets the name of the syntax specified by this syntax element.
   */
  public void setName(Syntax syntax, String name) {
    StringLiteral literal = ProtobufFactory.eINSTANCE.createStringLiteral();
    literal.getChunks().add(Strings.quote(name));
    syntax.setName(literal);
  }
}
