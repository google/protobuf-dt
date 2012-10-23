/*
 * Copyright (c) 2011 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.conversion;

import static org.eclipse.xtext.util.Strings.convertToJavaString;

import static com.google.eclipse.protobuf.util.Strings.removeLineBreaks;
import static com.google.eclipse.protobuf.util.Strings.unquote;

import org.eclipse.xtext.conversion.ValueConverterException;
import org.eclipse.xtext.conversion.impl.AbstractLexerBasedConverter;
import org.eclipse.xtext.nodemodel.INode;

import com.google.common.base.Function;

/**
 * Converts multi-line strings to {@code String}s.
 *
 * @author alruiz@google.com (Alex Ruiz)
 */
public class STRINGValueConverter extends AbstractLexerBasedConverter<String> {
  @Override protected String toEscapedString(String value) {
    if (value == null) {
      return null;
    }
    // TODO check if we really need to quote
    return '"' + toValue(value) + '"';
  }

  /**
   * Creates a {@code String} from the given input, if the given input represents a multiple-line string.
   * @param string the given input.
   * @param node the parsed node including hidden parts.
   * @return the new {@code String}.
   * @throws ValueConverterException if the given input has illegal characters.
   */
  @Override public String toValue(String string, INode node) throws ValueConverterException {
    if (string == null) {
      return null;
    }
    try {
      return toValue(string);
    } catch (RuntimeException e) {
      throw parsingError(string, node, e);
    }
  }

  private String toValue(String string) {
    return removeLineBreaks(string, LineTransformation.INSTANCE);
  }

  private ValueConverterException parsingError(String string, INode node, Exception cause) {
    return new ValueConverterException("Couldn't convert '" + string + "' to String.", node, cause);
  }

  private static class LineTransformation implements Function<String, String> {
    private static final LineTransformation INSTANCE = new LineTransformation();

    @Override public String apply(String input) {
      return convertToJavaString(unquote(input), true);
    }
  }
}
