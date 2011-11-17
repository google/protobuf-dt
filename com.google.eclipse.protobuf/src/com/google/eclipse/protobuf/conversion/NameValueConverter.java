/*
 * Copyright (c) 2011 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.conversion;

import com.google.eclipse.protobuf.protobuf.*;
import com.google.inject.Inject;

import org.eclipse.xtext.conversion.impl.AbstractLexerBasedConverter;
import org.eclipse.xtext.nodemodel.INode;

/**
 * Converts names to <code>{@link Name}</code>s.
 * 
 * @author alruiz@google.com (Alex Ruiz)
 */
public class NameValueConverter extends AbstractLexerBasedConverter<Name> {

  private ProtobufFactory factory = ProtobufFactory.eINSTANCE;
  @Inject private Keywords keywords;
  
  @Override public Name toValue(String string, INode node) {
    String value = value(string, node);
    if (value == null) return null;
    Name name = factory.createName();
    name.setValue(value);
    return name;
  }
  
  private String value(String string, INode node) {
    if (string != null) return string;
    String text = node.getText();
    if (text == null) return text;
    text = text.trim();
    return keywords.isKeyword(text) ? text : null;
  }
}
