/*
 * Copyright (c) 2011 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.ui.preferences.pages.compiler;

import com.google.eclipse.protobuf.ui.preferences.*;
import com.google.eclipse.protobuf.ui.preferences.binding.Binding;

/**
 * @author alruiz@google.com (Alex Ruiz)
 */
class BindingToCodeGeneration implements Binding {

  private final CodeGenerationSetting codeGeneration;
  private final BooleanPreference enabled;
  private final StringPreference outputDirectory;

  static BindingBuilder bindCodeGeneration(CodeGenerationSetting codeGeneration) {
    return new BindingBuilder(codeGeneration);
  }

  private BindingToCodeGeneration(CodeGenerationSetting codeGeneration, BooleanPreference enabled, StringPreference outputDirectory) {
    this.codeGeneration = codeGeneration;
    this.enabled = enabled;
    this.outputDirectory = outputDirectory;
  }

  /** {@inheritDoc} */
  @Override public void applyPreferenceValueToTarget() {
    codeGeneration.enabled(enabled.value());
    codeGeneration.outputDirectory(outputDirectory.value());
  }

  /** {@inheritDoc} */
  @Override public void applyDefaultPreferenceValueToTarget() {
    codeGeneration.enabled(enabled.defaultValue());
    codeGeneration.outputDirectory(outputDirectory.defaultValue());
  }

  /** {@inheritDoc} */
  @Override public void savePreferenceValue() {
    enabled.value(codeGeneration.isEnabled());
    outputDirectory.value(codeGeneration.outputDirectory());
  }

  static class BindingBuilder {
    private final CodeGenerationSetting codeGeneration;

    BindingBuilder(CodeGenerationSetting codeGeneration) {
      this.codeGeneration = codeGeneration;
    }

    BindingToCodeGeneration to(BooleanPreference enabled, StringPreference outputDirectory) {
      return new BindingToCodeGeneration(codeGeneration, enabled, outputDirectory);
    }
  }
}
