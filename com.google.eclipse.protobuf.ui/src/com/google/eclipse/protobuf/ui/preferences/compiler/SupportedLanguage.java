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
 * Languages supported by protoc.
 *
 * @author alruiz@google.com (Alex Ruiz)
 */
public class SupportedLanguage {

  public static final SupportedLanguage JAVA = new SupportedLanguage("Java", "java");
  public static final SupportedLanguage CPP = new SupportedLanguage("C++", "cpp");
  public static final SupportedLanguage PYTHON = new SupportedLanguage("Python", "python");

  private final String name;
  private final String code;

  private SupportedLanguage(String name, String code) {
    this.name = name;
    this.code = code;
  }

  /**
   * Returns this language's name.
   * @return this language's name.
   */
  public String name() {
    return name;
  }

  /**
   * Returns this language's code.
   * @return this language's code.
   */
  public String code() {
    return code;
  }

  /**
   * Returns all the supported languages.
   * @return all the supported languages.
   */
  public static SupportedLanguage[] values() {
    return new SupportedLanguage[] { JAVA, CPP, PYTHON };
  }
}