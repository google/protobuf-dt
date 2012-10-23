/*
 * Copyright (c) 2011 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.ui.documentation;

import static com.google.common.collect.Lists.newArrayList;

import java.util.Iterator;
import java.util.List;
import java.util.regex.Pattern;

/**
 * @author alruiz@google.com (Alex Ruiz)
 */
class Patterns implements Iterable<Pattern> {
  static Patterns compileAll(String...patterns) {
    return new Patterns(patterns);
  }

  private final List<Pattern> patterns = newArrayList();

  private Patterns(String[] patterns) {
    for (String s : patterns) {
      this.patterns.add(Pattern.compile(s));
    }
  }

  @Override public Iterator<Pattern> iterator() {
    return patterns.iterator();
  }
}
