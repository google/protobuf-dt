/*
 * Copyright (c) 2012 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.cdt.cpplang;

import static com.google.common.collect.ImmutableList.of;

import com.google.common.collect.ImmutableList;
import com.google.inject.Singleton;

/**
 * C++ keywords.
 *
 * @author alruiz@google.com (Alex Ruiz)
 */
@Singleton public class CppKeywords {

  private static final ImmutableList<String> KEYWORDS = of("and", "and_eq", "asm", "auto", "bitand", "bitor", "bool",
      "break", "case", "catch", "char", "class", "compl", "const", "const_cast", "continue", "default", "delete",
      "double", "dynamic_cast", "else", "enum", "explicit", "export", "extern", "false", "float", "for", "friend",
      "goto", "if", "inline", "int", "long", "mutable", "namespace", "new", "not", "not_eq", "operator", "or", "or_eq",
      "private", "protected", "public", "register", "reinterpret_cast", "return", "short", "signed", "sizeof",
      "static", "static_cast", "struct", "switch", "template", "this", "throw", "true", "try", "typedef", "typeid",
      "typename", "union", "unsigned", "using", "virtual", "void", "volatile", "wchar_t", "while", "xor", "xor_eq");

  @SuppressWarnings("unused")
  private static final ImmutableList<String> CPP11_KEYWORDS = of("alignas", "alignof", "char16_t", "char32_t",
      "constexpr", "decltype", "noexcept", "nullptr", "static_assert", "thread_local");

  /**
   * Indicates whether the content of the given {@code String} is a C++ keyword.
   * @param s the given {@code String}.
   * @return {@code true} if the content of the given {@code String} is a C++ keyword, {@code false} otherwise.
   */
  public boolean isKeyword(String s) {
    return KEYWORDS.contains(s);
  }
}
