/*
 * Copyright (c) 2011 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.junit.core;

import static java.io.File.separator;

import static org.junit.Assert.assertTrue;

import java.io.File;

/**
 * @author alruiz@google.com (Alex Ruiz)
 */
final class GeneratedProtoFiles {
  private static final String PARENT_DIRECTORY_NAME = "test-protos";

  static File protoFile(String fileName) {
    return new File(PARENT_DIRECTORY_NAME + separator + fileName);
  }

  static void ensureParentDirectoryExists() {
    File parent = new File(PARENT_DIRECTORY_NAME);
    if (!parent.isDirectory()) {
      assertTrue(parent.mkdir());
    }
  }

  private GeneratedProtoFiles() {}
}
