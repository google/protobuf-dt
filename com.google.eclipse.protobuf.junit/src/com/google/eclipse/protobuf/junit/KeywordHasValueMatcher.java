/*
 * Copyright (c) 2011 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.junit;

import org.eclipse.xtext.Keyword;
import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;

/**
 * Matches that verifies that a value of a <code>{@link Keyword}</code> is equal to the expected value.
 *
 * @author alruiz@google.com (Alex Ruiz)
 */
public class KeywordHasValueMatcher extends BaseMatcher<Keyword> {

  private final String expectedValue;

  public static KeywordHasValueMatcher hasValue(String expected) {
    return new KeywordHasValueMatcher(expected);
  }
  
  private KeywordHasValueMatcher(String expectedValue) {
    this.expectedValue = expectedValue;
  }
  
  public boolean matches(Object arg) {
    if (!(arg instanceof Keyword)) return false;
    Keyword keyword = (Keyword) arg;
    return keyword.getValue().equals(expectedValue);
  }

  public void describeTo(Description description) {
    description.appendValue(expectedValue);
  }
}
