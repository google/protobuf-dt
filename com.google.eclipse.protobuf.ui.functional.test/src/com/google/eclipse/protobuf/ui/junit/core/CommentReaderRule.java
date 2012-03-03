/*
 * Copyright (c) 2012 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.ui.junit.core;

import java.util.List;

import org.junit.rules.MethodRule;
import org.junit.runners.model.*;

import com.google.eclipse.protobuf.junit.core.CommentReader;

/**
 * JUnit <code>{@link MethodRule}</code> that keeps a registry of comments per method.
 *
 * @author alruiz@google.com (Alex Ruiz)
 */
public class CommentReaderRule implements MethodRule {
  private final CommentReader reader = new CommentReader();

  private List<String> comments;

  @Override public Statement apply(Statement base, FrameworkMethod method, Object target) {
    comments = reader.commentsIn(method);
    return base;
  }

  public List<String> commentsInCurrentTestMethod() {
    return comments;
  }
}
