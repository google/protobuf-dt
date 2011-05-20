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
import static com.google.eclipse.protobuf.ui.preferences.compiler.TargetLanguage.*;

import org.eclipse.jface.preference.IPreferenceStore;

/**
 * Contains a <code>{@link TargetLanguage}</code> per language supported by protoc.
 *
 * @author alruiz@google.com (Alex Ruiz)
 */
public class TargetLanguagePreferences {

  private final TargetLanguagePreference javaPreference;
  private final TargetLanguagePreference cppPreference;
  private final TargetLanguagePreference pythonPreference;

  TargetLanguagePreferences(IPreferenceStore store) {
    javaPreference = javaPreference(store);
    cppPreference = cppPreference(store);
    pythonPreference = pythonPreference(store);
  }

  private static TargetLanguagePreference javaPreference(IPreferenceStore store) {
    boolean enabled = store.getBoolean(GENERATE_JAVA_CODE);
    String outputDirectory = store.getString(JAVA_OUTPUT_DIRECTORY);
    return new TargetLanguagePreference(JAVA, outputDirectory, enabled);
  }

  private static TargetLanguagePreference cppPreference(IPreferenceStore store) {
    boolean enabled = store.getBoolean(GENERATE_CPP_CODE);
    String outputDirectory = store.getString(CPP_OUTPUT_DIRECTORY);
    return new TargetLanguagePreference(CPP, outputDirectory, enabled);
  }

  private static TargetLanguagePreference pythonPreference(IPreferenceStore store) {
    boolean enabled = store.getBoolean(GENERATE_PYTHON_CODE);
    String outputDirectory = store.getString(PYTHON_OUTPUT_DIRECTORY);
    return new TargetLanguagePreference(PYTHON, outputDirectory, enabled);
  }

  public TargetLanguagePreference java() {
    return javaPreference;
  }

  public TargetLanguagePreference cpp() {
    return cppPreference;
  }

  public TargetLanguagePreference python() {
    return pythonPreference;
  }
}