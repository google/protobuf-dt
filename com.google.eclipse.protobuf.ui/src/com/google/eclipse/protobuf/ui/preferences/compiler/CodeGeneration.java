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
 * Indicates whether code generation for a specific language is enabled and where the generated code should be placed.
 *
 * @author alruiz@google.com (Alex Ruiz)
 */
public class CodeGeneration {

  private final SupportedLanguage language;

  private String outputDirectory;
  private boolean enabled;

  CodeGeneration(SupportedLanguage language, String outputDirectory, boolean enabled) {
    this.language = language;
    this.outputDirectory = outputDirectory;
    this.enabled = enabled;
  }

  /**
   * Returns the supported language.
   * @return the supported language.
   */
  public SupportedLanguage language() {
    return language;
  }

  /**
   * Returns the name of the output directory.
   * @return the name of the output directory.
   */
  public String outputDirectory() {
    return outputDirectory;
  }

  /**
   * Indicates whether the language in this preference is enabled or not.
   * @return {@code true} if the language is enabled; {@code false} otherwise.
   */
  public boolean isEnabled() {
    return enabled;
  }

  /**
   * Updates the name of the output directory.
   * @param newVal the new name.
   */
  public void outputDirectory(String newVal) {
    outputDirectory = newVal;
  }

  /**
   * Enables or disables the language in this preference.
   * @param newVal indicates whether the language is enabled or not.
   */
  public void enabled(boolean newVal) {
    enabled = newVal;
  }
}
