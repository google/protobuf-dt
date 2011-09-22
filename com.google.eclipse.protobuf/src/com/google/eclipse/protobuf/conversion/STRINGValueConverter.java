/*
 * Copyright (c) 2011 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.conversion;

import static org.eclipse.xtext.util.Strings.*;

import org.eclipse.xtext.conversion.ValueConverterException;
import org.eclipse.xtext.conversion.impl.AbstractLexerBasedConverter;
import org.eclipse.xtext.nodemodel.INode;

import java.util.regex.Pattern;

/**
 * Converts multi-line strings to {@code String}s.
 *
 * @author alruiz@google.com (Alex Ruiz)
 */
public class STRINGValueConverter extends AbstractLexerBasedConverter<String> {

  private static final Pattern LINE_BREAK = Pattern.compile("\"[\t\r\n]+\"|'[\t\r\n]+'");
  
  @Override
  protected String toEscapedString(String value) {
    if (value == null) return null;
    return '"' + convertToJavaString(removeLineBreaksFrom(value), false) + '"';
  }

  /**
   * Creates a {@code String} from the given input, if the given input represents a multi-line string.
   * @param string the given input.
   * @param node the parsed node including hidden parts.
   * @return the new integer.
   * @throws ValueConverterException if the given input has illegal characters.
   */
  public String toValue(String string, INode node) throws ValueConverterException {
    if (string == null) return null;
    try {
      String clean = removeLineBreaksFrom(string).trim();
      return convertToJavaString(clean.substring(1, clean.length() - 1), true);
    } catch (IllegalArgumentException e) {
      throw parsingError(string, node, e);
    }
  }

  private static String removeLineBreaksFrom(String s) {
    if (isEmpty(s)) return s;
    return LINE_BREAK.matcher(s).replaceAll("");
  }

  private ValueConverterException parsingError(String string, INode node, Exception cause) {
    return new ValueConverterException("Couldn't convert '" + string + "' to String.", node, cause);
  }
}
