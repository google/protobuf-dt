/*
 * Copyright (c) 2011 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.ui.preferences.compiler;

/**
 * @author alruiz@google.com (Alex Ruiz)
 */
final class PreferenceNames {

  static final String ENABLE_PROJECT_SETTINGS = "compiler.enableProjectSettings";
  static final String COMPILE_PROTO_FILES = "compiler.compileProtoFiles";
  static final String USE_PROTOC_IN_SYSTEM_PATH = "compiler.useProtocInSystemPath";
  static final String USE_PROTOC_IN_CUSTOM_PATH = "compiler.useProtocInCustomPath";
  static final String PROTOC_FILE_PATH = "compiler.protocFilePath";
  static final String GENERATE_JAVA_CODE = "compiler.javaCodeGenerationEnabled";
  static final String GENERATE_CPP_CODE = "compiler.cppCodeGenerationEnabled";
  static final String GENERATE_PYTHON_CODE = "compiler.pythonCodeGenerationEnabled";
  static final String JAVA_OUTPUT_DIRECTORY = "compiler.javaOutputDirectory";
  static final String CPP_OUTPUT_DIRECTORY = "compiler.cppOutputDirectory";
  static final String PYTHON_OUTPUT_DIRECTORY = "compiler.pythonOutputDirectory";
  static final String REFRESH_RESOURCES = "compiler.refreshResources";
  static final String REFRESH_PROJECT = "compiler.refreshProject";
  static final String REFRESH_OUTPUT_DIRECTORY = "compiler.refreshOutputDirectory";

  private PreferenceNames() {}
}
