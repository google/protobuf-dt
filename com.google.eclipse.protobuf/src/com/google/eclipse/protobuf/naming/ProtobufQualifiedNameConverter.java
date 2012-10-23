/*
 * Copyright (c) 2012 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.naming;

import static org.eclipse.xtext.util.Strings.isEmpty;

import static com.google.eclipse.protobuf.util.Strings.removeLineBreaksFrom;

import java.util.regex.Pattern;

import org.eclipse.xtext.naming.IQualifiedNameConverter.DefaultImpl;
import org.eclipse.xtext.naming.QualifiedName;

import com.google.inject.Singleton;

/**
 * Provides support for multi-line qualified names.
 *
 * @author alruiz@google.com (Alex Ruiz)
 */
@Singleton public class ProtobufQualifiedNameConverter extends DefaultImpl {
  private final Pattern delimiterPattern = Pattern.compile(delimiterPlusWhitespace());

  private String delimiterPlusWhitespace() {
    return "\\s*" + Pattern.quote(getDelimiter()) + "\\s*";
  }

  /**
   * Splits the given {@code String} into segments and returns them as a <code>{@link QualifiedName}</code>.
   * @param s the given input.
   * @throws IllegalArgumentException if the input is empty or {@code null}.
   */
  @Override public QualifiedName toQualifiedName(String s) {
    if (isEmpty(s)) {
      throw new IllegalArgumentException("Qualified name cannot be null or empty");
    }
    String withoutLineBreaks = removeLineBreaksFrom(s);
    String[] segments = delimiterPattern.split(withoutLineBreaks);
    return QualifiedName.create(segments);
  }
}
