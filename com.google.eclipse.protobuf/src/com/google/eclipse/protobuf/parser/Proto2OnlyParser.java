/*
 * Copyright (c) 2011 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.parser;

import org.antlr.runtime.CharStream;
import org.eclipse.xtext.nodemodel.*;
import org.eclipse.xtext.nodemodel.impl.NodeModelBuilder;
import org.eclipse.xtext.parser.IParseResult;

import com.google.eclipse.protobuf.parser.antlr.ProtobufParser;
import com.google.eclipse.protobuf.protobuf.Protobuf;

/**
 * Parser that only parses protocol buffers with "proto2" syntax, older syntax is ignored completely.
 *
 * @author alruiz@google.com (Alex Ruiz)
 */
public class Proto2OnlyParser extends ProtobufParser {

  private static final String[] ERRORS_TO_LOOK_FOR = { "missing EOF at 'c'", "missing EOF at 'java'",
      "missing EOF at 'parsed'", "missing EOF at 'python'", "no viable alternative at input '<'" };

  @Override protected IParseResult doParse(String ruleName, CharStream in, NodeModelBuilder builder,
      int initialLookAhead) {
    IParseResult result = super.doParse(ruleName, in, builder, initialLookAhead);
    // TODO enable this check via preferences in internal version.
    // if (isNonProto2(result)) {
    //  return new ParseResult(new NonProto2Protobuf(), result.getRootNode(), false);
    // }
    return result;
  }

  private boolean isNonProto2(IParseResult result) {
    if (!result.hasSyntaxErrors()) {
      return false;
    }
    for (INode node : result.getSyntaxErrors()) {
      if (isNonProto2(node.getSyntaxErrorMessage())) {
        return true;
      }
    }
    Protobuf root = (Protobuf) result.getRootASTElement();
    if (root != null && root.getSyntax() == null) {
      return true;
    }
    return false;
  }

  private boolean isNonProto2(SyntaxErrorMessage syntaxErrorMessage) {
    if (syntaxErrorMessage == null) {
      return false;
    }
    String message = syntaxErrorMessage.getMessage();
    for (String nonProto2Keyword : ERRORS_TO_LOOK_FOR) {
      if (message.contains(nonProto2Keyword)) {
        return true;
      }
    }
    return false;
  }
}
