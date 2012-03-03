/*
 * Copyright (c) 2011 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.formatting;

import static com.google.eclipse.protobuf.grammar.CommonKeyword.*;
import static com.google.eclipse.protobuf.util.CommonWords.space;

import org.eclipse.xtext.Keyword;
import org.eclipse.xtext.formatting.impl.*;

import com.google.eclipse.protobuf.services.ProtobufGrammarAccess;

/**
 * Provides custom formatting.
 *
 * @author alruiz@google.com (Alex Ruiz)
 *
 * @see <a href="http://www.eclipse.org/Xtext/documentation/2_0_0/105-formatting.php">Xtext Formatting</a>
 */
public class ProtobufFormatter extends AbstractDeclarativeFormatter {
  @Override protected void configureFormatting(FormattingConfig c) {
    ProtobufGrammarAccess g = (ProtobufGrammarAccess) getGrammarAccess();
    c.setLinewrap(0, 1, 2).before(g.getSL_COMMENTRule());
    c.setLinewrap(1).after(g.getPackageRule());
    c.setLinewrap(1).after(g.getNormalImportRule());
    c.setLinewrap(1).after(g.getPublicImportRule());
    c.setLinewrap(1).after(g.getNativeOptionRule());
    c.setLinewrap(1).after(g.getCustomOptionRule());
    c.setLinewrap(1).after(g.getGroupRule());
    c.setLinewrap(1).after(g.getMessageFieldRule());
    c.setLinewrap(1).after(g.getEnumRule());
    c.setLinewrap(1).after(g.getEnumElementRule());
    c.setLinewrap(1).after(g.getRpcRule());
    for (Keyword k : g.findKeywords(EQUAL.toString())) {
      c.setSpace(space()).around(k);
    }
    for (Keyword k : g.findKeywords(SEMICOLON.toString())) {
      c.setNoSpace().before(k);
    }
    for (Keyword k : g.findKeywords(OPENING_CURLY_BRACKET.toString())) {
      c.setIndentationIncrement().after(k);
      c.setLinewrap(1).after(k);
    }
    for (Keyword k : g.findKeywords(CLOSING_CURLY_BRACKET.toString())) {
      c.setIndentationDecrement().before(k);
      c.setLinewrap(2).after(k);
    }
    for (Keyword k : g.findKeywords(OPENING_BRACKET.toString())) {
      c.setNoSpace().after(k);
    }
    for (Keyword k : g.findKeywords(CLOSING_BRACKET.toString())) {
      c.setNoSpace().before(k);
    }
  }
}
