/*
 * Copyright (c) 2012 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.ui.scoping;

import static org.eclipse.xtext.util.Strings.isEmpty;

import java.io.File;

import com.google.inject.Singleton;

/**
 * @author alruiz@google.com (Alex Ruiz)
 */
@Singleton class FileSystemPathResolver {
  String resolvePath(String path) {
    if (isEmpty(path)) {
      return null;
    }
    File directory = new File(path);
    if (!directory.isDirectory()) {
      return null;
    }
    return directory.toURI().getPath();
  }
}
