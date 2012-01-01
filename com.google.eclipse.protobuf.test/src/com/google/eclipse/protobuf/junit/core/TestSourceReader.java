/*
 * Copyright (c) 2011 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License v1.0 which
 * accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.junit.core;

import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Maps.newHashMap;
import static java.io.File.separator;

import java.io.*;
import java.util.*;

import org.junit.runners.model.FrameworkMethod;

import com.google.eclipse.protobuf.junit.util.MultiLineTextBuilder;

/**
 * @author alruiz@google.com (Alex Ruiz)
 */
class TestSourceReader {
  private static final String COMMENT_START = "//";

  private final Map<String, List<String>> comments = newHashMap();
  private final CommentProcessor processor = new CommentProcessor();

  private boolean initialized;

  private final Object lock = new Object();

  String commentsIn(FrameworkMethod method) {
    synchronized (lock) {
      ensureCommentsAreRead(method.getMethod().getDeclaringClass());
      List<String> allComments = comments.get(method.getName());
      if (allComments == null || allComments.isEmpty()) {
        return null;
      }
      for (String comment : allComments) {
        Object processed = processor.processComment(comment);
        if (processed instanceof String) {
          return (String) processed;
        }
      }
      return null;
    }
  }

  private void ensureCommentsAreRead(Class<?> testClass) {
    if (initialized) {
      return;
    }
    doReadComments(testClass);
    initialized = true;
  }

  private void doReadComments(Class<?> testClass) {
    String fqn = testClass.getName().replace('.', '/');
    fqn = fqn.indexOf("$") == -1 ? fqn : fqn.substring(0, fqn.indexOf("$"));
    String classFile = fqn + ".java";
    File file = new File("src" + separator + classFile);
    Scanner scanner = null;
    List<String> allComments = newArrayList();
    MultiLineTextBuilder comment = new MultiLineTextBuilder();
    try {
      scanner = new Scanner(new FileInputStream(file));
      String line;
      while (scanner.hasNextLine()) {
        line = scanner.nextLine().replaceFirst("^\\s*", "");
        if (line.startsWith(COMMENT_START)) {
          comment.append(line.substring(COMMENT_START.length()).trim());
          continue;
        }
        if (comment.isEmpty()) {
          continue;
        }
        line = line.trim();
        String testName = testName(line);
        if (line.length() == 0 || testName != null) {
          if (!allComments.contains(comment)) {
            allComments.add(comment.toString());
          }
          comment = new MultiLineTextBuilder();
        }
        if (testName != null) {
          comments.put(testName, allComments);
          allComments = newArrayList();
        }
      }
    } catch (IOException e) {
      e.printStackTrace();
    } finally {
      scanner.close();
    }
  }

  private static String testName(String line) {
    if (!line.startsWith("@Test")) {
      return null;
    }
    int indexOfShould = line.indexOf("should");
    return (indexOfShould == -1) ? null : line.substring(indexOfShould, line.indexOf("("));
  }
}
