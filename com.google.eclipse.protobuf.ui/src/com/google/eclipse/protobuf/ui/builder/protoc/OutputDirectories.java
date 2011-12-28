/*
 * Copyright (c) 2011 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.ui.builder.protoc;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;

import com.google.eclipse.protobuf.ui.preferences.compiler.core.CompilerPreferences;

/**
 * @author alruiz@google.com (Alex Ruiz)
 */
class OutputDirectories {

  private final OutputDirectory java;
  private final OutputDirectory cpp;
  private final OutputDirectory python;

  OutputDirectories(IProject project, CompilerPreferences preferences) throws CoreException {
    java = new OutputDirectory(project, preferences.javaCodeGenerationEnabled(), preferences.javaOutputDirectory());
    cpp = new OutputDirectory(project, preferences.cppCodeGenerationEnabled(), preferences.cppOutputDirectory());
    python = new OutputDirectory(project, preferences.pythonCodeGenerationEnabled(),
        preferences.pythonOutputDirectory());
  }

  OutputDirectory java() {
    return java;
  }

  OutputDirectory cpp() {
    return cpp;
  }

  OutputDirectory python() {
    return python;
  }
}
