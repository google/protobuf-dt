/*
 * Copyright (c) 2011 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.ui.preferences.compiler;

import static com.google.eclipse.protobuf.ui.preferences.compiler.CompilerPreferenceNames.*;

import org.eclipse.core.resources.IProject;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.xtext.ui.editor.preferences.IPreferenceStoreAccess;

/**
 * Compiler preferences, retrieved from an <code>{@link IPreferenceStore}</code>.
 *
 * @author alruiz@google.com (Alex Ruiz)
 */
public class CompilerPreferences {

  public final boolean compileProtoFiles;
  public final String protocPath;
  public final CompilerTargetLanguage language;
  public final String outputFolderName;
  public final boolean refreshResources;
  public final PostCompilationRefreshTarget refreshTarget;

  public static CompilerPreferences loadPreferences(IPreferenceStoreAccess access, IProject project) {
    IPreferenceStore store = access.getWritablePreferenceStore(project);
    boolean useProjectPreferences = store.getBoolean(ENABLE_PROJECT_SETTINGS);
    if (!useProjectPreferences) store = access.getWritablePreferenceStore();
    return new CompilerPreferences(store);
  }

  private CompilerPreferences(IPreferenceStore store) {
    compileProtoFiles = store.getBoolean(COMPILE_PROTO_FILES);
    boolean useProtocInSystemPath = store.getBoolean(USE_PROTOC_IN_SYSTEM_PATH);
    protocPath = (useProtocInSystemPath) ? "protoc" : store.getString(PROTOC_FILE_PATH);
    language = CompilerTargetLanguage.readFrom(store);
    outputFolderName = store.getString(OUTPUT_FOLDER_NAME);
    refreshResources = store.getBoolean(REFRESH_RESOURCES);
    refreshTarget = PostCompilationRefreshTarget.readFrom(store);
  }
}
