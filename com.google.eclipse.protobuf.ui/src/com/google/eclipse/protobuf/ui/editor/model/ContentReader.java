/*
 * Copyright (c) 2011 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.ui.editor.model;

import static com.google.common.io.Closeables.closeQuietly;
import static com.google.eclipse.protobuf.util.Encodings.UTF_8;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

import com.google.inject.Singleton;

/**
 * @author alruiz@google.com (Alex Ruiz)
 */
@Singleton class ContentReader {
  private static final int DEFAULT_FILE_SIZE = 15 * 1024;

  String contentsOf(InputStream inputStream) throws IOException {
    Reader reader = null;
    try {
      reader = new BufferedReader(readerFor(inputStream), DEFAULT_FILE_SIZE);
      StringBuilder contents = new StringBuilder(DEFAULT_FILE_SIZE);
      char[] buffer = new char[2048];
      int character = reader.read(buffer);
      while (character > 0) {
        contents.append(buffer, 0, character);
        character = reader.read(buffer);
      }
      return contents.toString();
    } finally {
      closeQuietly(reader);
    }
  }

  private Reader readerFor(InputStream inputStream) throws IOException {
    return new InputStreamReader(inputStream, UTF_8);
  }
}
