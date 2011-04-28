/*
 * Copyright (c) 2011 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.ui.grammar;

import org.eclipse.xtext.Keyword;

/**
 * Set of common keywords (it may or may not include all the keywords in the grammar.)
 *
 * @author alruiz@google.com (Alex Ruiz)
 */
public enum CommonKeyword {

  BOOL("bool"), TRUE("true"), FALSE("false"), BYTES("bytes"), OPENING_BRACKET("["), CLOSING_BRACKET("]"),
  DEFAULT("default"), EQUAL("="), PACKED("packet"), SEMICOLON(":"), STRING("string");

  public final String value;

  private CommonKeyword(String value) {
    this.value = value;
  }

  public boolean hasValueEqualTo(Keyword k) {
    return hasValueEqualTo(k.getValue());
  }

  public boolean hasValueEqualTo(String s) {
    return value.equals(s);
  }
}
