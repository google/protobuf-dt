/*
 * Copyright (c) 2011 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.grammar;

import static org.eclipse.xtext.GrammarUtil.getAllKeywords;

import java.util.Set;

import org.eclipse.xtext.IGrammarAccess;

import com.google.inject.Inject;
import com.google.inject.Singleton;

/**
 * Utility methods related to grammar keywords.
 *
 * @author alruiz@google.com (Alex Ruiz)
 */
@Singleton public class Keywords {
  private final Set<String> keywords;

  /**
   * Creates a new <code>{@link Keywords}</code>.
   * @param grammarAccess provides access to elements of the protocol buffer language.
   */
  @Inject public Keywords(IGrammarAccess grammarAccess) {
    keywords = getAllKeywords(grammarAccess.getGrammar());
  }

  /**
   * Indicates whether the given {@code String} is a keyword or not.
   * @param s the given {@code String}.
   * @return {@code true} if the given {@code String} is a keyword, {@code false} otherwise.
   */
  public boolean isKeyword(String s) {
    return keywords.contains(s);
  }
}
