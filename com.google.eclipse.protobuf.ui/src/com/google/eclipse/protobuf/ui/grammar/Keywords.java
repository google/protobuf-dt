/*
 * Copyright (c) 2011 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.ui.grammar;

import static org.eclipse.xtext.GrammarUtil.containedKeywords;

import java.util.List;

import org.eclipse.xtext.*;

import com.google.inject.*;

/**
 * @author alruiz@google.com (Alex Ruiz)
 */
@Singleton
public class Keywords {

  private Keyword bool;
  private Keyword boolTrue;
  private Keyword boolFalse;
  private Keyword openingBracket;
  private Keyword closingBracket;
  private Keyword defaultValue;
  private Keyword equalSign;
  private Keyword packed;
  private Keyword semicolon;
  private Keyword string;

  @Inject public Keywords(IGrammarAccess grammarAccess) {
    List<Keyword> allKeywords = containedKeywords(grammarAccess.getGrammar());
    for (Keyword k : allKeywords) {
      if (assignIfIsBool(k)) continue;
      if (assignIfIsBoolTrue(k)) continue;
      if (assignIfIsBoolFalse(k)) continue;
      if (assignIfIsOpeningBracket(k)) continue;
      if (assignIfIsClosingBracket(k)) continue;
      if (assignIfIsDefaultValue(k)) continue;
      if (assignIfIsEqualSign(k)) continue;
      if (assignIfIsPacked(k)) continue;
      if (assignIfIsSemicolon(k)) continue;
      if (assignIfIsString(k)) continue;
    }
  }

  private boolean assignIfIsBool(Keyword k) {
    if (!isKeywordValue(k, "bool")) return false;
    bool = k;
    return true;
  }

  private boolean assignIfIsBoolTrue(Keyword k) {
    if (!isKeywordValue(k, "true")) return false;
    boolTrue = k;
    return true;
  }

  private boolean assignIfIsBoolFalse(Keyword k) {
    if (!isKeywordValue(k, "false")) return false;
    boolFalse = k;
    return true;
  }

  private boolean assignIfIsOpeningBracket(Keyword k) {
    if (!isKeywordValue(k, "[")) return false;
    openingBracket = k;
    return true;
  }

  private boolean assignIfIsClosingBracket(Keyword k) {
    if (!isKeywordValue(k, "]")) return false;
    closingBracket = k;
    return true;
  }

  private boolean assignIfIsDefaultValue(Keyword k) {
    if (!isKeywordValue(k, "default")) return false;
    defaultValue = k;
    return true;
  }

  private boolean assignIfIsEqualSign(Keyword k) {
    if (!isKeywordValue(k, "=")) return false;
    equalSign = k;
    return true;
  }

  private boolean assignIfIsPacked(Keyword k) {
    if (!isKeywordValue(k, "packed")) return false;
    packed = k;
    return true;
  }

  private boolean assignIfIsSemicolon(Keyword k) {
    if (!isKeywordValue(k, ";")) return false;
    semicolon = k;
    return true;
  }

  private boolean assignIfIsString(Keyword k) {
    if (!isKeywordValue(k, "string")) return false;
    string = k;
    return true;
  }

  private static boolean isKeywordValue(Keyword k, String expectedValue) {
    return expectedValue.equals(k.getValue());
  }

  public Keyword bool() {
    return bool;
  }

  public Keyword boolTrue() {
    return boolTrue;
  }

  public Keyword boolFalse() {
    return boolFalse;
  }

  public Keyword openingBracket() {
    return openingBracket;
  }

  public Keyword closingBracket() {
    return closingBracket;
  }

  public Keyword defaultValue() {
    return defaultValue;
  }

  public Keyword equalSign() {
    return equalSign;
  }

  public Keyword packed() {
    return packed;
  }

  public Keyword semicolon() {
    return semicolon;
  }

  public Keyword string() {
    return string;
  }
}
