/*
 * Copyright (c) 2012 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.junit.core;

import static java.io.File.separator;
import static java.util.Collections.emptyList;

import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Maps.newHashMap;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import org.junit.runners.model.FrameworkMethod;

import com.google.eclipse.protobuf.junit.util.MultiLineTextBuilder;

/**
 * Reads the comments of test methods.
 *
 * @author alruiz@google.com (Alex Ruiz)
 */
public class CommentReader {
  private static final String COMMENT_START = "//";

  private final Map<String, List<String>> commentsByMethod = newHashMap();
  private boolean initialized;

  private final Object lock = new Object();

  public List<String> commentsIn(FrameworkMethod method) {
    synchronized (lock) {
      ensureCommentsAreRead(method.getMethod().getDeclaringClass());
      List<String> comments = commentsByMethod.get(method.getName());
      if (comments != null) {
        return comments;
      }
      return emptyList();
    }
  }

  private void ensureCommentsAreRead(Class<?> testClass) {
    if (initialized) {
      return;
    }
    readComments(testClass);
    initialized = true;
  }

  private void readComments(Class<?> testClass) {
    String fqn = testClass.getName().replace('.', '/');
    fqn = fqn.indexOf("$") == -1 ? fqn : fqn.substring(0, fqn.indexOf("$"));
    String classFile = fqn + ".java";
    File file = new File("src" + separator + classFile);
    Scanner scanner = null;
    List<String> comments = newArrayList();
    MultiLineTextBuilder comment = new MultiLineTextBuilder();
    try {
      scanner = new Scanner(new FileInputStream(file));
      String line;
      while (scanner.hasNextLine()) {
        line = scanner.nextLine().replaceFirst("^\\s*", "");
        if (line.startsWith(COMMENT_START)) {
          String text = line.substring(COMMENT_START.length());
          if (text.startsWith(" ")) {
            text = text.substring(1);
          }
          comment.append(text);
          continue;
        }
        if (comment.isEmpty()) {
          continue;
        }
        line = line.trim();
        String testName = testName(line);
        if (line.length() == 0 || testName != null) {
          if (!comments.contains(comment)) {
            comments.add(comment.toString());
          }
          comment = new MultiLineTextBuilder();
        }
        if (testName != null) {
          commentsByMethod.put(testName, comments);
          comments = newArrayList();
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
