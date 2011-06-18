/*
 * Copyright (c) 2011 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.ui.preferences.pages.compiler;

import static java.util.Collections.unmodifiableList;

import java.util.*;

import org.eclipse.jface.preference.IPreferenceStore;

import com.google.inject.Singleton;

/**
 * Reads "code generation" preferences.
 *
 * @author alruiz@google.com (Alex Ruiz)
 */
@Singleton
public class CodeGenerationPreferencesProvider {

  public List<CodeGeneration> getPreferences(IPreferenceStore store) {
    List<CodeGeneration> options = new ArrayList<CodeGeneration>();
    for (SupportedLanguage language : SupportedLanguage.values()) options.add(read(store, language));
    return unmodifiableList(options);
  }

  private static CodeGeneration read(IPreferenceStore store, SupportedLanguage language) {
    String outputDirectory = store.getString(outputDirectoryPreferenceName(language));
    boolean enabled = store.getBoolean(codeGenerationEnabledPreferenceName(language));
    return new CodeGeneration(language, outputDirectory, enabled);
  }

  public List<CodeGeneration> getDefaults(IPreferenceStore store) {
    List<CodeGeneration> options = new ArrayList<CodeGeneration>();
    for (SupportedLanguage language : SupportedLanguage.values()) options.add(readDefault(store, language));
    return unmodifiableList(options);
  }

  private static CodeGeneration readDefault(IPreferenceStore store, SupportedLanguage language) {
    String outputDirectory = store.getDefaultString(outputDirectoryPreferenceName(language));
    boolean enabled = store.getDefaultBoolean(codeGenerationEnabledPreferenceName(language));
    return new CodeGeneration(language, outputDirectory, enabled);
  }

  public void save(IPreferenceStore store, List<CodeGeneration> options) {
    for (CodeGeneration option : options) save(store, option);
  }

  private static void save(IPreferenceStore store, CodeGeneration option) {
    SupportedLanguage language = option.language();
    store.setValue(outputDirectoryPreferenceName(language), option.outputDirectory());
    store.setValue(codeGenerationEnabledPreferenceName(language), option.isEnabled());
  }

  private static String outputDirectoryPreferenceName(SupportedLanguage language) {
    return "compiler." + language.code() + "OutputDirectory";
  }

  private static String codeGenerationEnabledPreferenceName(SupportedLanguage language) {
    return "compiler." + language.code() + "CodeGenerationEnabled";
  }
}