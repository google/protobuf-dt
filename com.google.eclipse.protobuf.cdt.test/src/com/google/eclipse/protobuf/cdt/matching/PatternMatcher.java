/*
 * Copyright (c) 2012 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.cdt.matching;

import java.util.regex.Pattern;

import org.hamcrest.*;

/**
 * @author alruiz@google.com (Alex Ruiz)
 */
class PatternMatcher extends TypeSafeMatcher<String> {
  private final Pattern pattern;

  static PatternMatcher matches(Pattern pattern) {
    return new PatternMatcher(pattern);
  }

  private PatternMatcher(Pattern pattern) {
    super(String.class);
    this.pattern = pattern;
  }

  @Override public boolean matchesSafely(String item) {
    return pattern.matcher(item).matches();
  }

  @Override public void describeTo(Description description) {
    description.appendValue(pattern.pattern());
  }

}
