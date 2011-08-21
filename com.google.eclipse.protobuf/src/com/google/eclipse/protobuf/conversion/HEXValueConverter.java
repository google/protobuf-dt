/*
 * Copyright (c) 2011 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.conversion;

import static org.eclipse.xtext.util.Strings.isEmpty;

import org.eclipse.xtext.conversion.ValueConverterException;
import org.eclipse.xtext.conversion.impl.AbstractLexerBasedConverter;
import org.eclipse.xtext.nodemodel.INode;

/**
 * Converts hexadecimal numbers to {@code int}s.
 *
 * @author alruiz@google.com (Alex Ruiz)
 */
public class HEXValueConverter extends AbstractLexerBasedConverter<Integer> {

  /**
   * Creates am {@code int} from the given input, if the given input represents an hexadecimal number.
   * @param string the given input.
   * @param node the parsed node including hidden parts.
   * @return the new integer.
   * @throws ValueConverterException if the given input is {@code null}, empty or does not represent an hexadecimal
   * number.
   */
  public Integer toValue(String string, INode node) throws ValueConverterException {
    if (isEmpty(string)) throw new ValueConverterException("Couldn't convert empty string to int.", node, null);
    int length = string.length();
    if (length < 3) throw parsingError(string, node);
    if (!string.substring(0, 2).equalsIgnoreCase("0x")) throw parsingError(string, node);
    String val = string.substring(2, length);
    try {
      return Integer.parseInt(val, 16);
    } catch (NumberFormatException e) {
      throw parsingError(string, node, e);
    }
  }

  private ValueConverterException parsingError(String string, INode node) {
    return parsingError(string, node, null);
  }

  private ValueConverterException parsingError(String string, INode node, Exception cause) {
    return new ValueConverterException("Couldn't convert '" + string + "' to int.", node, cause);
  }
}
