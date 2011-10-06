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

import com.google.eclipse.protobuf.junit.util.MultiLineTextBuilder;

import org.junit.runners.model.FrameworkMethod;

import java.io.*;
import java.util.*;

/**
 * @author alruiz@google.com (Alex Ruiz)
 */
class TestSourceReader {

  private final Map<String, MultiLineTextBuilder> comments = new HashMap<String, MultiLineTextBuilder>();
  private final Object lock = new Object();
  private boolean initialized;
  
  private String testName(String line) {
    if (!line.startsWith("@Test")) return null;
    int indexOfShould = line.indexOf("should");
    return (indexOfShould == -1) ? null : line.substring(indexOfShould, line.indexOf("("));
  }
 
  String commentsIn(FrameworkMethod method) {
    synchronized (lock) {
      ensureCommentsAreRead(method.getMethod().getDeclaringClass());
      MultiLineTextBuilder text = comments.get(method.getName());
      return (text == null) ? null : text.toString();
    }
  }
  
  private void ensureCommentsAreRead(Class<?> testClass) {
    if (initialized) return;
    doReadComments(testClass);
    initialized = true;
  }
  
  private void doReadComments(Class<?> testClass) {
    String fqn = testClass.getName().replace('.', '/');
    fqn = fqn.indexOf("$") == -1 ? fqn : fqn.substring(0, fqn.indexOf("$"));
    String classFile = fqn + ".java";
    File file = new File("src" + File.separator + classFile);
    BufferedReader reader = null;
    MultiLineTextBuilder text = new MultiLineTextBuilder();
    try {
      reader = new BufferedReader(new FileReader(file));
      String line;
      while ((line = reader.readLine()) != null) {
        line = line.replaceFirst("^\\s*", "");
        if (line.startsWith("//")) {
          text.append(line.substring(2));
          continue;
        }
        if (text.isEmpty()) continue;
        String testName = testName(line);
        if (testName != null) {
          comments.put(testName, text);
          text = new MultiLineTextBuilder();
        }
      }
    } catch (IOException e) {
      e.printStackTrace();
    } finally {
      if (reader != null) {
        try {
          reader.close();
        } catch (IOException e) {}
      }
    }
  }
}
