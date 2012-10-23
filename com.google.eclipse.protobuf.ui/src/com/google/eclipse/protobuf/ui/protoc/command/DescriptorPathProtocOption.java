/*
 * Copyright (c) 2012 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.ui.protoc.command;

import static java.io.File.separator;

import static org.eclipse.xtext.util.Strings.concat;
import static org.eclipse.xtext.util.Strings.isEmpty;

import static com.google.common.collect.Lists.newArrayList;

import com.google.common.annotations.VisibleForTesting;
import com.google.eclipse.protobuf.ui.preferences.compiler.CompilerPreferences;

/**
 * @author alruiz@google.com (Alex Ruiz)
 */
class DescriptorPathProtocOption implements ProtocOption {
  private final CompilerPreferences preferences;
  private final String descriptorFqn;

  private boolean initialized;
  private String descriptorPath;

  DescriptorPathProtocOption(CompilerPreferences preferences) {
    this(preferences, separator);
  }

  @VisibleForTesting DescriptorPathProtocOption(CompilerPreferences preferences, String pathSeparator) {
    this.preferences = preferences;
    descriptorFqn = concat(pathSeparator, newArrayList("", "google", "protobuf", "descriptor.proto"));
  }

  @Override public void addOptionTo(ProtocCommand command) {
    if (!initialized) {
      initialize();
    }
    if (!isEmpty(descriptorPath)) {
      command.appendOption("proto_path", descriptorPath);
    }
  }

  private void initialize() {
    initialized = true;
    String fullPath = preferences.descriptorPath();
    if (!isEmpty(fullPath)) {
      int indexOfDescriptorFqn = fullPath.indexOf(descriptorFqn);
      if (indexOfDescriptorFqn == -1) {
        String format = "Path '%s' does not contain '%s'";
        throw new IllegalArgumentException(String.format(format, fullPath, descriptorFqn));
      }
      descriptorPath = fullPath.substring(0, indexOfDescriptorFqn);
    }
  }
}
