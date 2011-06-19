/*
 * Copyright (c) 2011 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.ui.preferences.pages.compiler;

import static com.google.eclipse.protobuf.ui.preferences.pages.compiler.SupportedLanguage.*;
import static java.util.Arrays.asList;
import static java.util.Collections.unmodifiableList;

import java.util.List;

/**
 * All the supported <code>{@link CodeGenerationSetting}</code>s.
 *
 * @author alruiz@google.com (Alex Ruiz)
 */
public class CodeGenerationSettings {

  private final CodeGenerationSetting java;
  private final CodeGenerationSetting cpp;
  private final CodeGenerationSetting python;

  private final List<CodeGenerationSetting> allSettings;

  CodeGenerationSettings() {
    java = new CodeGenerationSetting(JAVA);
    cpp = new CodeGenerationSetting(CPP);
    python = new CodeGenerationSetting(PYTHON);
    allSettings = unmodifiableList(asList(java, cpp, python));
  }

  /**
   * Returns the settings for code generation using Java.
   * @return the settings for code generation using Java.
   */
  public CodeGenerationSetting java() {
    return java;
  }

  /**
   * Returns the settings for code generation using C++.
   * @return the settings for code generation using C++.
   */
  public CodeGenerationSetting cpp() {
    return cpp;
  }

  /**
   * Returns the settings for code generation using Python.
   * @return the settings for code generation using Python.
   */
  public CodeGenerationSetting python() {
    return python;
  }

  /**
   * Returns all the settings for code generation using all supported languages.
   * @return all the settings for code generation using all supported languages.
   */
  public List<CodeGenerationSetting> allSettings() {
    return allSettings;
  }
}
