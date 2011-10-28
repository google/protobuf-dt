/*
 * Copyright (c) 2011 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.junit.core;

import static com.google.eclipse.protobuf.junit.core.GeneratedProtoFiles.*;
import static java.util.regex.Pattern.compile;

import java.io.*;
import java.util.Scanner;
import java.util.regex.*;

/**
 * @author alruiz@google.com (Alex Ruiz)
 */
class CommentProcessor {

  private static final Pattern CREATE_FILE_PATTERN = compile("// Create file (.*)");

  Object processComment(String comment) {
    Scanner scanner = new Scanner(comment);
    String fileName = null;
    while (scanner.hasNextLine()) {
      String line = scanner.nextLine().trim();
      Matcher matcher = CREATE_FILE_PATTERN.matcher(line);
      if (!matcher.matches()) return comment;
      fileName = matcher.group(1);
      break;
    }
    return createFile(fileName, comment);
  }

  private File createFile(String fileName, String contents) {
    ensureParentDirectoryExists();
    File file = protoFile(fileName);
    if (file.isFile()) file.delete();
    Writer out = null;
    try {
      out = new OutputStreamWriter(new FileOutputStream(file));
      out.write(contents);
    } catch (IOException e) {
      e.printStackTrace();
    } finally {
      closeQuietly(out);
    }
    return file;
  }

  private void closeQuietly(Writer out) {
    if (out == null) return;
    try {
      out.close();
    } catch (IOException e) {}
  }
}
