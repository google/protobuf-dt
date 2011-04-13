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

import com.google.inject.*;

/**
 * @author alruiz@google.com (Alex Ruiz)
 */
@Singleton
public class CompoundElements {

  private static final String EMPTY_STRING = "\"\"";

  private final String inBracketsFormat;

  private final String defaultValue;
  private final String defaultValueInBrackets;
  private final String defaultStringValue;
  private final String defaultStringValueInBrackets;
  private final String packed;
  private final String packedInBrackets;

  @Inject public CompoundElements(Keywords keywords) {
    inBracketsFormat = keywords.openingBracket().getValue() + "%s" + keywords.closingBracket().getValue();
    defaultValue = format("%s %s", keywords.defaultValue(), keywords.equalSign());
    defaultValueInBrackets = inBrackets(defaultValue);
    defaultStringValue = format("%s %s %s", keywords.defaultValue(), keywords.equalSign(), EMPTY_STRING);
    defaultStringValueInBrackets = inBrackets(defaultStringValue);
    packed = format("%s %s %s", keywords.packed(), keywords.equalSign(), keywords.boolTrue());
    packedInBrackets = inBrackets(packed);
  }

  private static String format(String format, Object...values) {
    int count = values.length;
    Object[] cleanValues = new Object[count];
    for (int i = 0; i < count; i++) {
      Object value = values[i];
      cleanValues[i] = (value instanceof Keyword) ? ((Keyword) value).getValue() : value;
    }
    return String.format(format, cleanValues);
  }

  private String inBrackets(String element) {
    return String.format(inBracketsFormat, element);
  }

  public String defaultValue() {
    return defaultValue;
  }

  public String defaultValueInBrackets() {
    return defaultValueInBrackets;
  }

  public String defaultStringValue() {
    return defaultStringValue;
  }

  public String defaultStringValueInBrackets() {
    return defaultStringValueInBrackets;
  }

  public String emptyString() {
    return EMPTY_STRING;
  }

  public String packed() {
    return packed;
  }

  public String packedInBrackets() {
    return packedInBrackets;
  }
}
