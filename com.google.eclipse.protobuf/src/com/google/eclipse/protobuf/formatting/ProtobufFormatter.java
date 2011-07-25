/*
 * Copyright (c) 2011 Google Inc.
 * 
 * All rights reserved. This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License v1.0 which
 * accompanies this distribution, and is available at
 * 
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.formatting;

import static com.google.eclipse.protobuf.grammar.CommonKeyword.*;
import static com.google.eclipse.protobuf.util.CommonWords.space;

import com.google.eclipse.protobuf.services.*;
import com.google.eclipse.protobuf.services.ProtobufGrammarAccess.EnumElements;
import com.google.eclipse.protobuf.services.ProtobufGrammarAccess.ExtendMessageElements;
import com.google.eclipse.protobuf.services.ProtobufGrammarAccess.MessageElements;
import com.google.eclipse.protobuf.services.ProtobufGrammarAccess.RpcElements;
import com.google.eclipse.protobuf.services.ProtobufGrammarAccess.ServiceElements;

import org.eclipse.xtext.Keyword;
import org.eclipse.xtext.formatting.impl.*;

/**
 * This class provides custom formatting.
 * 
 * @see <a href="http://www.eclipse.org/Xtext/documentation/2_0_0/105-formatting.php">Xtext Formatting</a>
 */
public class ProtobufFormatter extends AbstractDeclarativeFormatter {

  @Override protected void configureFormatting(FormattingConfig c) {
    ProtobufGrammarAccess g = (ProtobufGrammarAccess) getGrammarAccess();
    c.setLinewrap(0, 1, 2).before(g.getSL_COMMENTRule());
    c.setLinewrap(1).after(g.getPropertyRule());
    for (Keyword equal : g.findKeywords(EQUAL.toString())) {
      c.setSpace(space()).around(equal);
    }
    for (Keyword semicolon : g.findKeywords(SEMICOLON.toString())) {
      c.setNoSpace().before(semicolon);
    }
    for (Keyword openingBrace : g.findKeywords("{")) {
      c.setLinewrap(1).after(openingBrace);
    }
    for (Keyword closingBrace : g.findKeywords("}")) {
      c.setLinewrap(2).after(closingBrace);
    }
    for (Keyword openingBracket : g.findKeywords(OPENING_BRACKET.toString())) {
      c.setNoSpace().after(openingBracket);
    }
    for (Keyword closingBracket : g.findKeywords(CLOSING_BRACKET.toString())) {
      c.setNoSpace().before(closingBracket);
    }
    indentMessageElements(c, g);
    indentExtendMessageElements(c, g);
    indentEnumElements(c, g);
    indentServiceElements(c, g);
    indentRpcElements(c, g);
  }

  private void indentMessageElements(FormattingConfig c, ProtobufGrammarAccess g) {
    MessageElements e = g.getMessageAccess();
    c.setIndentationIncrement().after(e.getLeftCurlyBracketKeyword_2());
    c.setIndentationDecrement().before(e.getRightCurlyBracketKeyword_4());
  }
  
  private void indentExtendMessageElements(FormattingConfig c, ProtobufGrammarAccess g) {
    ExtendMessageElements e = g.getExtendMessageAccess();
    c.setIndentationIncrement().after(e.getLeftCurlyBracketKeyword_2());
    c.setIndentationDecrement().before(e.getRightCurlyBracketKeyword_4());
  }
  
  private void indentEnumElements(FormattingConfig c, ProtobufGrammarAccess g) {
    EnumElements e = g.getEnumAccess();
    c.setIndentationIncrement().after(e.getLeftCurlyBracketKeyword_2());
    c.setIndentationDecrement().before(e.getRightCurlyBracketKeyword_4());
  }

  private void indentServiceElements(FormattingConfig c, ProtobufGrammarAccess g) {
    ServiceElements e = g.getServiceAccess();
    c.setIndentationIncrement().after(e.getLeftCurlyBracketKeyword_2());
    c.setIndentationDecrement().before(e.getRightCurlyBracketKeyword_4());
  }

  private void indentRpcElements(FormattingConfig c, ProtobufGrammarAccess g) {
    RpcElements e = g.getRpcAccess();
    c.setIndentationIncrement().after(e.getLeftCurlyBracketKeyword_9_0_0_0());
    c.setIndentationDecrement().before(e.getRightCurlyBracketKeyword_9_0_0_2());
  }
}
