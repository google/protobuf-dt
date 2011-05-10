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

import org.eclipse.jface.preference.IPreferenceStore;

/**
 * Languages supported by protoc.
 *
 * @author alruiz@google.com (Alex Ruiz)
 */
public enum TargetLanguage {

  JAVA, CPP, PYTHON;

  // TODO check if protoc can generate more than one language sources at the same time.
  static TargetLanguage find(IPreferenceStore store) {
    if (store.getBoolean(GENERATE_JAVA_CODE)) return JAVA;
    if (store.getBoolean(GENERATE_CPP_CODE)) return CPP;
    if (store.getBoolean(GENERATE_PYTHON_CODE)) return PYTHON;
    return JAVA;
  }
}