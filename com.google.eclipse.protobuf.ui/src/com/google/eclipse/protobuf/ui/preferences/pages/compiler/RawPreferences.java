/*
 * Copyright (c) 2011 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.ui.preferences.pages.compiler;

import org.eclipse.jface.preference.IPreferenceStore;

import com.google.eclipse.protobuf.ui.preferences.*;

/**
 * @author alruiz@google.com (Alex Ruiz)
 */
class RawPreferences {

  private final BooleanPreference compileProtoFiles;
  private final BooleanPreference useProtocInSystemPath;
  private final BooleanPreference useProtocInCustomPath;
  private final StringPreference protocPath;
  private final StringPreference descriptorPath;
  private final BooleanPreference javaCodeGenerationEnabled;
  private final BooleanPreference cppCodeGenerationEnabled;
  private final BooleanPreference pythonCodeGenerationEnabled;
  private final StringPreference javaOutputDirectory;
  private final StringPreference cppOutputDirectory;
  private final StringPreference pythonOutputDirectory;
  private final BooleanPreference refreshResources;
  private final BooleanPreference refreshProject;
  private final BooleanPreference refreshOutputDirectory;

  RawPreferences(IPreferenceStore store) {
    compileProtoFiles = new BooleanPreference("compiler.compileProtoFiles", store);
    useProtocInSystemPath = new BooleanPreference("compiler.useProtocInSystemPath", store);
    useProtocInCustomPath = new BooleanPreference("compiler.useProtocInCustomPath", store);
    protocPath = new StringPreference("compiler.protocFilePath", store);
    descriptorPath = new StringPreference("compiler.descriptorFilePath", store);
    javaCodeGenerationEnabled = new BooleanPreference("compiler.javaCodeGenerationEnabled", store);
    cppCodeGenerationEnabled = new BooleanPreference("compiler.cppCodeGenerationEnabled", store);
    pythonCodeGenerationEnabled = new BooleanPreference("compiler.pythonCodeGenerationEnabled", store);
    javaOutputDirectory = new StringPreference("compiler.javaOutputDirectory", store);
    cppOutputDirectory = new StringPreference("compiler.cppOutputDirectory", store);
    pythonOutputDirectory = new StringPreference("compiler.pythonOutputDirectory", store);
    refreshResources = new BooleanPreference("compiler.refreshResources", store);
    refreshProject = new BooleanPreference("compiler.refreshProject", store);
    refreshOutputDirectory = new BooleanPreference("compiler.refreshOutputDirectory", store);
  }

  BooleanPreference compileProtoFiles() {
    return compileProtoFiles;
  }

  BooleanPreference useProtocInSystemPath() {
    return useProtocInSystemPath;
  }

  BooleanPreference useProtocInCustomPath() {
    return useProtocInCustomPath;
  }

  StringPreference protocPath() {
    return protocPath;
  }

  StringPreference descriptorPath() {
    return descriptorPath;
  }

  BooleanPreference javaCodeGenerationEnabled() {
    return javaCodeGenerationEnabled;
  }

  BooleanPreference cppCodeGenerationEnabled() {
    return cppCodeGenerationEnabled;
  }

  BooleanPreference pythonCodeGenerationEnabled() {
    return pythonCodeGenerationEnabled;
  }

  StringPreference javaOutputDirectory() {
    return javaOutputDirectory;
  }

  StringPreference cppOutputDirectory() {
    return cppOutputDirectory;
  }

  StringPreference pythonOutputDirectory() {
    return pythonOutputDirectory;
  }

  BooleanPreference refreshResources() {
    return refreshResources;
  }

  BooleanPreference refreshProject() {
    return refreshProject;
  }

  BooleanPreference refreshOutputDirectory() {
    return refreshOutputDirectory;
  }
}
