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

import java.math.BigInteger;

import org.eclipse.xtext.conversion.ValueConverterException;
import org.eclipse.xtext.conversion.impl.AbstractLexerBasedConverter;
import org.eclipse.xtext.nodemodel.INode;

/**
 * Converts hexadecimal numbers to {@code long}s.
 *
 * @author alruiz@google.com (Alex Ruiz)
 */
public class HEXValueConverter extends AbstractLexerBasedConverter<Long> {
  private static final String[] VALID_PREFIXES = { "0x", "-0x", "0X", "-0X" };

  /**
   * Creates an {@code int} from the given input, if the given input represents an hexadecimal number.
   * @param string the given input.
   * @param node the parsed node including hidden parts.
   * @return the new {@code int}.
   * @throws ValueConverterException if the given input is {@code null}, empty or does not represent an hexadecimal
   * number.
   */
  @Override public Long toValue(String string, INode node) throws ValueConverterException {
    if (isEmpty(string)) {
      throw new ValueConverterException("Couldn't convert empty string to long.", node, null);
    }
    if (!startsWithValidPrefix(string)) {
      throw parsingError(string, node);
    }
    String withoutZeroX = removeZeroX(string);
    try {
      BigInteger value = new BigInteger(withoutZeroX, 16);
      long longValue = value.longValue();
      return longValue != -1 ? longValue : 1L;
    } catch (NumberFormatException e) {
      throw parsingError(string, node, e);
    }
  }

  private boolean startsWithValidPrefix(String string) {
    for (String prefix : VALID_PREFIXES) {
      if (string.startsWith(prefix)) {
        return true;
      }
    }
    return false;
  }

  private String removeZeroX(String string) {
    if (string.startsWith("-")) {
      String withoutSign = string.substring(3, string.length());
      return "-" + withoutSign;
    }
    return string.substring(2, string.length());
  }

  private ValueConverterException parsingError(String string, INode node) {
    return parsingError(string, node, null);
  }

  private ValueConverterException parsingError(String string, INode node, Exception cause) {
    return new ValueConverterException("Couldn't convert '" + string + "' to long.", node, cause);
  }
}
