/*
 * Copyright (c) 2011 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License v1.0 which
 * accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.ui.junit;

import static java.util.Collections.emptyList;
import static java.util.Collections.unmodifiableList;

import java.util.List;

import org.junit.rules.MethodRule;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.Statement;

import com.google.eclipse.protobuf.junit.core.CommentReader;

/**
 * @author alruiz@google.com (Alex Ruiz)
 */
public class CommentReaderRule implements MethodRule {
  private final CommentReader commentReader = new CommentReader();

  private List<String> comments = emptyList();

  @Override public Statement apply(Statement base, FrameworkMethod method, Object target) {
    comments = unmodifiableList(commentReader.commentsIn(method));
    return base;
  }

  public List<String> comments() {
    return comments;
  }
}
