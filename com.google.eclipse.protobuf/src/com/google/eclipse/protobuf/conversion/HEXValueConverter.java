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
 * @author alruiz@google.com (Alex Ruiz)
 */
public class HEXValueConverter extends AbstractLexerBasedConverter<Integer> {

  public Integer toValue(String string, INode node) throws ValueConverterException {
    if (isEmpty(string)) throw new ValueConverterException("Couldn't convert empty string to int.", node, null);
    int length = string.length();
    if (length < 3) throw parsingError(string, node);
    if (!string.substring(0, 2).equalsIgnoreCase("0x")) throw parsingError(string, node);
    String val = string.substring(2, length);
    int parsed = Integer.parseInt(val, 16);
    return parsed;
  }

  private ValueConverterException parsingError(String string, INode node) {
    return parsingError(string, node, null);
  }

  private ValueConverterException parsingError(String string, INode node, Exception cause) {
    return new ValueConverterException("Couldn't convert '" + string + "' to int.", node, cause);
  }
}
