/*
 * Copyright (c) 2011 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.ui.preferences.compiler;

import static com.google.eclipse.protobuf.ui.preferences.compiler.PreferenceNames.*;
import static com.google.eclipse.protobuf.ui.preferences.compiler.SupportedLanguage.*;

import org.eclipse.jface.preference.IPreferenceStore;

/**
 * Contains a <code>{@link SupportedLanguage}</code> per language supported by protoc.
 *
 * @author alruiz@google.com (Alex Ruiz)
 */
public class CodeGenerationOptions {

  private final CodeGenerationOption javaPreference;
  private final CodeGenerationOption cppPreference;
  private final CodeGenerationOption pythonPreference;

  CodeGenerationOptions(IPreferenceStore store) {
    javaPreference = javaPreference(store);
    cppPreference = cppPreference(store);
    pythonPreference = pythonPreference(store);
  }

  private static CodeGenerationOption javaPreference(IPreferenceStore store) {
    boolean enabled = store.getBoolean(GENERATE_JAVA_CODE);
    String outputDirectory = store.getString(JAVA_OUTPUT_DIRECTORY);
    return new CodeGenerationOption(JAVA, outputDirectory, enabled);
  }

  private static CodeGenerationOption cppPreference(IPreferenceStore store) {
    boolean enabled = store.getBoolean(GENERATE_CPP_CODE);
    String outputDirectory = store.getString(CPP_OUTPUT_DIRECTORY);
    return new CodeGenerationOption(CPP, outputDirectory, enabled);
  }

  private static CodeGenerationOption pythonPreference(IPreferenceStore store) {
    boolean enabled = store.getBoolean(GENERATE_PYTHON_CODE);
    String outputDirectory = store.getString(PYTHON_OUTPUT_DIRECTORY);
    return new CodeGenerationOption(PYTHON, outputDirectory, enabled);
  }

  public CodeGenerationOption java() {
    return javaPreference;
  }

  public CodeGenerationOption cpp() {
    return cppPreference;
  }

  public CodeGenerationOption python() {
    return pythonPreference;
  }
}