/*
 * Copyright (c) 2011 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.ui.preferences.compiler.core;

import org.eclipse.core.resources.IProject;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.xtext.ui.editor.preferences.IPreferenceStoreAccess;

import com.google.eclipse.protobuf.ui.preferences.*;

/**
 * "Compiler" preferences, retrieved from an <code>{@link IPreferenceStore}</code>.
 *
 * @author alruiz@google.com (Alex Ruiz)
 */
public class CompilerPreferences {

  public static final String ENABLE_PROJECT_SETTINGS_PREFERENCE_NAME = "compiler.enableProjectSettings";

  /**
   * Creates a new <code>{@link CompilerPreferences}</code>.
   * @param storeAccess simplified access to Eclipse's preferences.
   * @param project the current project.
   * @return the created {@code CompilerPreferences}.
   */
  public static CompilerPreferences compilerPreferences(IPreferenceStoreAccess storeAccess, IProject project) {
    IPreferenceStore store = storeAccess.getWritablePreferenceStore(project);
    boolean enableProjectSettings = store.getBoolean(ENABLE_PROJECT_SETTINGS_PREFERENCE_NAME);
    if (!enableProjectSettings) {
      store = storeAccess.getWritablePreferenceStore();
    }
    return new CompilerPreferences(store);
  }

  private final BooleanPreference enableProjectSettings;
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

  /**
   * Creates a new <code>{@link CompilerPreferences}</code>.
   * @param store a table mapping named preferences to values.
   */
  public CompilerPreferences(IPreferenceStore store) {
    enableProjectSettings = new BooleanPreference(ENABLE_PROJECT_SETTINGS_PREFERENCE_NAME, store);
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

  /**
   * Returns the setting that specifies whether project project should be used instead of workspace preferences.
   * @return the setting that specifies whether project project should be used instead of workspace preferences.
   */
  public BooleanPreference enableProjectSettings() {
    return enableProjectSettings;
  }

  /**
   * Returns the setting that specifies whether protoc should be called after saving a file.
   * @return the setting that specifies whether protoc should be called after saving a file.
   */
  public BooleanPreference compileProtoFiles() {
    return compileProtoFiles;
  }

  /**
   * Returns the setting that specifies whether the editor should call the version of protoc in the system path.
   * @return the setting that specifies whether the editor should call the version of protoc in the system path.
   */
  public BooleanPreference useProtocInSystemPath() {
    return useProtocInSystemPath;
  }

  /**
   * Returns the setting that specifies whether the editor should call a version of protoc stored in a user-specified
   * path in the file system.
   * @return the setting that specifies whether the editor should call a version of protoc stored in a user-specified
   * path in the file system.
   */
  public BooleanPreference useProtocInCustomPath() {
    return useProtocInCustomPath;
  }

  /**
   * Returns the setting that specifies the user-specified path of protoc.
   * @return the setting that specifies the user-specified path of protoc.
   */
  public StringPreference protocPath() {
    return protocPath;
  }

  /**
   * Returns the setting that specifies the path of the file descriptor.proto. The path is needed by protoc only if the
   * file to compile imports descriptor.proto.
   * @return the setting that specifies the path of the file descriptor.proto.
   */
  public StringPreference descriptorPath() {
    return descriptorPath;
  }

  /**
   * Returns the setting that specifies whether protoc should generate Java code.
   * @return the setting that specifies whether protoc should generate Java code.
   */
  public BooleanPreference javaCodeGenerationEnabled() {
    return javaCodeGenerationEnabled;
  }

  /**
   * Returns the setting that specifies whether protoc should generate C++ code.
   * @return the setting that specifies whether protoc should generate C++ code.
   */
  public BooleanPreference cppCodeGenerationEnabled() {
    return cppCodeGenerationEnabled;
  }

  /**
   * Returns the setting that specifies whether protoc should generate Python code.
   * @return the setting that specifies whether protoc should generate Python code.
   */
  public BooleanPreference pythonCodeGenerationEnabled() {
    return pythonCodeGenerationEnabled;
  }

  /**
   * Returns the setting that specifies the directory where to store the generated Java code. This setting is available
   * only if the value of <code>{@link #javaCodeGenerationEnabled()}</code> is {@code true}.
   * @return the setting that specifies the directory where to store the generated Java code.
   */
  public StringPreference javaOutputDirectory() {
    return javaOutputDirectory;
  }

  /**
   * Returns the setting that specifies the directory where to store the generated C++ code. This setting is available
   * only if the value of <code>{@link #cppCodeGenerationEnabled()}</code> is {@code true}.
   * @return the setting that specifies the directory where to store the generated C++ code.
   */
  public StringPreference cppOutputDirectory() {
    return cppOutputDirectory;
  }

  /**
   * Returns the setting that specifies the directory where to store the generated Python code. This setting is
   * available only if the value of <code>{@link #pythonCodeGenerationEnabled()}</code> is {@code true}.
   * @return the setting that specifies the directory where to store the generated Python code.
   */
  public StringPreference pythonOutputDirectory() {
    return pythonOutputDirectory;
  }

  /**
   * Returns the setting that specifies whether resources should be refreshed after changing the "Compiler" preference
   * settings.
   * @return the setting that specifies whether resources should be refreshed after changing the "Compiler" preference
   * settings.
   */
  public BooleanPreference refreshResources() {
    return refreshResources;
  }

  /**
   * Returns the setting that specifies whether the current project should be refreshed after changing the "Compiler"
   * preference settings. This setting is enable only if the value of <code>{@link #refreshResources()}</code> is
   * {@code true}.
   * @return the setting that specifies whether the current project should be refreshed after changing
   */
  public BooleanPreference refreshProject() {
    return refreshProject;
  }

  /**
   * Returns the setting that specifies whether the directory containing generated code should be refreshed after
   * changing the "Compiler" preference settings. This setting is enable only if the value of
   * <code>{@link #refreshResources()}</code> is {@code true}.
   * @return the setting that specifies whether the current project should be refreshed after changing
   */
  public BooleanPreference refreshOutputDirectory() {
    return refreshOutputDirectory;
  }
}
