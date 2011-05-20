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

import org.eclipse.jface.preference.IPreferenceStore;

/**
 * Compiler preferences, retrieved from an <code>{@link IPreferenceStore}</code>.
 *
 * @author alruiz@google.com (Alex Ruiz)
 */
public class CompilerPreferences {

  private final boolean compileProtoFiles;
  private final String protocPath;
  private final CodeGenerationOptions languages;
  private final boolean refreshResources;
  private final PostCompilationRefreshTarget refreshTarget;

  CompilerPreferences(IPreferenceStore store) {
    compileProtoFiles = store.getBoolean(COMPILE_PROTO_FILES);
    boolean useProtocInSystemPath = store.getBoolean(USE_PROTOC_IN_SYSTEM_PATH);
    protocPath = (useProtocInSystemPath) ? "protoc" : store.getString(PROTOC_FILE_PATH);
    languages = new CodeGenerationOptions(store);
    refreshResources = store.getBoolean(REFRESH_RESOURCES);
    refreshTarget = PostCompilationRefreshTarget.readFrom(store);
  }

  public boolean shouldCompileProtoFiles() {
    return compileProtoFiles;
  }

  public String protocPath() {
    return protocPath;
  }

  public CodeGenerationOptions languages() {
    return languages;
  }

  public boolean shouldRefreshResources() {
    return refreshResources;
  }

  public PostCompilationRefreshTarget refreshTarget() {
    return refreshTarget;
  }


}
