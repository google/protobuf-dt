/*
 * Copyright (c) 2011 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.ui.preferences.pages.compiler;

import static com.google.eclipse.protobuf.ui.preferences.pages.compiler.PostCompilationRefreshTarget.*;

import org.eclipse.jface.preference.IPreferenceStore;

/**
 * Compiler preferences, retrieved from an <code>{@link IPreferenceStore}</code>.
 *
 * @author alruiz@google.com (Alex Ruiz)
 */
public class CompilerPreferences {

  private final boolean compileProtoFiles;
  private final String protocPath;
  private final String descriptorPath;
  private final CodeGenerationSettings codeGenerationSettings;
  private final boolean refreshResources;
  private final PostCompilationRefreshTarget refreshTarget;

  CompilerPreferences(RawPreferences preferences) {
    compileProtoFiles = preferences.compileProtoFiles().value();
    boolean useProtocInSystemPath = preferences.useProtocInSystemPath().value();
    protocPath = (useProtocInSystemPath) ? "protoc" : preferences.protocPath().value();
    descriptorPath = preferences.descriptorPath().value();
    codeGenerationSettings = new CodeGenerationSettings();
    codeGenerationSettings.java().enabled(preferences.javaCodeGenerationEnabled().value());
    codeGenerationSettings.java().outputDirectory(preferences.javaOutputDirectory().value());
    codeGenerationSettings.cpp().enabled(preferences.cppCodeGenerationEnabled().value());
    codeGenerationSettings.cpp().outputDirectory(preferences.cppOutputDirectory().value());
    codeGenerationSettings.python().enabled(preferences.pythonCodeGenerationEnabled().value());
    codeGenerationSettings.python().outputDirectory(preferences.pythonOutputDirectory().value());
    refreshResources = preferences.refreshResources().value();
    boolean refreshProject = preferences.refreshProject().value();
    refreshTarget = refreshProject ? PROJECT : OUTPUT_DIRECTORIES;
  }

  public boolean shouldCompileProtoFiles() {
    return compileProtoFiles;
  }

  public String protocPath() {
    return protocPath;
  }

  public String descriptorPath() {
    return descriptorPath;
  }

  public CodeGenerationSettings codeGenerationSettings() {
    return codeGenerationSettings;
  }

  public boolean shouldRefreshResources() {
    return refreshResources;
  }

  public PostCompilationRefreshTarget refreshTarget() {
    return refreshTarget;
  }
}
