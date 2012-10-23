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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import static com.google.eclipse.protobuf.junit.core.UnitTestModule.unitTestModule;
import static com.google.eclipse.protobuf.junit.core.XtextRule.overrideRuntimeModuleWith;

import org.eclipse.xtext.IGrammarAccess;
import org.junit.Rule;
import org.junit.Test;

import com.google.eclipse.protobuf.junit.core.XtextRule;
import com.google.inject.Inject;

/**
 * Tests for <code>{@link Keywords#isKeyword(String)}</code>.
 *
 * @author alruiz@google.com (Alex Ruiz)
 */
public class Keywords_isKeyword_Test {
  @Rule public XtextRule xtext = overrideRuntimeModuleWith(unitTestModule());

  @Inject private IGrammarAccess grammarAccess;
  @Inject private Keywords keywords;

  @Test public void should_return_true_if_given_String_is_keyword() {
    for (String s : getAllKeywords(grammarAccess.getGrammar())) {
      assertTrue(keywords.isKeyword(s));
    }
  }

  @Test public void should_return_false_if_given_String_is_not_keyword() {
    assertFalse(keywords.isKeyword("Google"));
  }
}
