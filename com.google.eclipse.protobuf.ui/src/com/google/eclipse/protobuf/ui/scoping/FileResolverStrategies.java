/*
 * Copyright (c) 2011 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.ui.scoping;

import static com.google.eclipse.protobuf.ui.preferences.paths.PathResolutionType.*;
import static com.google.inject.internal.Maps.newHashMap;

import java.util.Map;

import com.google.eclipse.protobuf.ui.preferences.paths.PathResolutionType;
import com.google.inject.Inject;
import com.google.inject.Singleton;

/**
 * @author alruiz@google.com (Alex Ruiz)
 */
@Singleton
class FileResolverStrategies {

  private final Map<PathResolutionType, FileResolverStrategy> strategies = newHashMap();

  @Inject FileResolverStrategies(Resources resources) {
    strategies.put(SINGLE_DIRECTORY, new SingleDirectoryFileResolver(resources));
    strategies.put(MULTIPLE_DIRECTORIES, new MultipleDirectoriesFileResolver(resources));
  }

  FileResolverStrategy strategyFor(PathResolutionType type) {
    return strategies.get(type);
  }
}
