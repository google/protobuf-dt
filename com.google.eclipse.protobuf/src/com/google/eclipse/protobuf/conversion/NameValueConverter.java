// Copyright 2011 Google Inc. All Rights Reserved.

package com.google.eclipse.protobuf.conversion;

import static org.eclipse.xtext.GrammarUtil.getAllKeywords;

import com.google.eclipse.protobuf.protobuf.*;
import com.google.inject.Inject;

import org.eclipse.xtext.*;
import org.eclipse.xtext.conversion.impl.*;
import org.eclipse.xtext.nodemodel.INode;

import java.util.*;

/**
 * @author alruiz@google.com (Alex Ruiz)
 * 
 */
public class NameValueConverter extends AbstractLexerBasedConverter<Name> {

  @Inject
  private IGrammarAccess grammarAccess;

  @Override public Name toValue(String string, INode node) {
    String value = value(string, node);
    if (value == null) return null;
    Name name = ProtobufFactory.eINSTANCE.createName();
    name.setValue(value);
    return name;
  }
  
  private String value(String string, INode node) {
    if (string != null) return string;
    String text = node.getText();
    if (text == null) return text;
    text = text.trim();
    Set<String> allKeywords = getAllKeywords(grammarAccess.getGrammar());
    return allKeywords.contains(text) ? text : null;
  }
}
