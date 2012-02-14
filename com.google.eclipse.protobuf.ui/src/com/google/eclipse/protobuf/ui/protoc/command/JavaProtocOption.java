/*
 * Copyright (c) 2012 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.ui.protoc.command;

import org.eclipse.core.resources.IProject;

import com.google.eclipse.protobuf.ui.preferences.*;
import com.google.eclipse.protobuf.ui.preferences.compiler.core.CompilerPreferences;

/**
 * @author alruiz@google.com (Alex Ruiz)
 */
class JavaProtocOption extends AbstractOutputDirectoryProtocOption {
  JavaProtocOption(CompilerPreferences preferences, IProject project) {
    super("java_out", preferences, project);
  }

  @Override BooleanPreference isEnabled(CompilerPreferences preferences) {
    return preferences.javaCodeGenerationEnabled();
  }

  @Override StringPreference outputDirectoryName(CompilerPreferences preferences) {
    return preferences.javaOutputDirectory();
  }
}
