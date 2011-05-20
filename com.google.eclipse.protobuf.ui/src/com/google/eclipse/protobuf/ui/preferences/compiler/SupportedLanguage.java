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
 * Languages supported by protoc.
 *
 * @author alruiz@google.com (Alex Ruiz)
 */
public class SupportedLanguage {

  public static final SupportedLanguage JAVA = new SupportedLanguage("Java");
  public static final SupportedLanguage CPP = new SupportedLanguage("C++");
  public static final SupportedLanguage PYTHON = new SupportedLanguage("Python");

  private final String name;

  private SupportedLanguage(String name) {
    this.name = name;
  }

  /**
   * Returns this language's name.
   * @return this language's name.
   */
  public String name() {
    return name;
  }

  // TODO check if protoc can generate more than one language sources at the same time.
  // TODO remove
  static SupportedLanguage readFrom(IPreferenceStore store) {
    if (store.getBoolean(GENERATE_JAVA_CODE)) return JAVA;
    if (store.getBoolean(GENERATE_CPP_CODE)) return CPP;
    if (store.getBoolean(GENERATE_PYTHON_CODE)) return PYTHON;
    return JAVA;
  }

  // TODO remove
  public static SupportedLanguage[] values() {
    return new SupportedLanguage[] { JAVA, CPP, PYTHON };
  }
}