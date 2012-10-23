/*
 * Copyright (c) 2011 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.conversion;

import static java.lang.Double.NEGATIVE_INFINITY;
import static java.lang.Double.NaN;
import static java.lang.Double.POSITIVE_INFINITY;

import static org.eclipse.xtext.util.Strings.isEmpty;

import static com.google.common.collect.Maps.newHashMap;

import java.util.Map;

import org.eclipse.xtext.conversion.ValueConverterException;
import org.eclipse.xtext.conversion.impl.AbstractLexerBasedConverter;
import org.eclipse.xtext.nodemodel.INode;

/**
 * Converts floating-point numbers to {@code double}s.
 *
 * @author alruiz@google.com (Alex Ruiz)
 */
public class DOUBLEValueConverter extends AbstractLexerBasedConverter<Double> {
  private static final Map<String, Double> PREDEFINED_VALUES = newHashMap();

  static {
    PREDEFINED_VALUES.put("nan", NaN);
    PREDEFINED_VALUES.put("inf", POSITIVE_INFINITY);
    PREDEFINED_VALUES.put("-inf", NEGATIVE_INFINITY);
  }

  /**
   * Creates an {@code float} from the given input, if the given input represents a floating-point number.
   * @param string the given input.
   * @param node the parsed node including hidden parts.
   * @return the new {@code float}.
   * @throws ValueConverterException if the given input is {@code null}, empty or does not represent a floating-point
   * number.
   */
  @Override public Double toValue(String string, INode node) throws ValueConverterException {
    if (isEmpty(string)) {
      throw new ValueConverterException("Couldn't convert empty string to double.", node, null);
    }
    Double predefinedValue = PREDEFINED_VALUES.get(string);
    if (predefinedValue != null) {
      return predefinedValue;
    }
    try {
      return Double.parseDouble(string);
    } catch (NumberFormatException e) {
      throw parsingError(string, node, e);
    }
  }

  private ValueConverterException parsingError(String string, INode node, Exception cause) {
    return new ValueConverterException("Couldn't convert '" + string + "' to double.", node, cause);
  }
}
