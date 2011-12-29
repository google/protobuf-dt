/*
 * Copyright (c) 2011 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.ui.scoping;

import com.google.eclipse.protobuf.ui.util.Resources;
import com.google.inject.Inject;

/**
 * @author alruiz@google.com (Alex Ruiz)
 */
class FileResolverStrategies {

  private final FileResolverStrategy singleDirectory;
  private final FileResolverStrategy multipleDirectories;

  @Inject FileResolverStrategies(PathMapping mapping, Resources resources) {
    singleDirectory = new SingleDirectoryFileResolver(resources);
    multipleDirectories = new MultipleDirectoriesFileResolver(mapping, resources);
  }

  FileResolverStrategy singleDirectory() {
    return singleDirectory;
  }

  FileResolverStrategy multipleDirectories() {
    return multipleDirectories;
  }
}
