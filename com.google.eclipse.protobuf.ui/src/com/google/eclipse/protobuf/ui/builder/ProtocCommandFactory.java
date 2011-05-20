/*
 * Copyright (c) 2011 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.ui.builder;

import java.util.*;

import org.eclipse.core.resources.IFile;

import com.google.eclipse.protobuf.ui.preferences.compiler.SupportedLanguage;

/**
 * @author alruiz@google.com (Alex Ruiz)
 */
class ProtocCommandFactory {

  private static final Map<SupportedLanguage, String> LANG_OUT_FLAG = new HashMap<SupportedLanguage, String>();

  static {
    for (SupportedLanguage lang : SupportedLanguage.values())
      LANG_OUT_FLAG.put(lang, "--" + lang.name().toLowerCase() + "_out=");
  }

  String protocCommand(IFile protoFile, String protocPath, List<String> importRoots, SupportedLanguage language,
      String outputFolderPath) {
    StringBuilder command = new StringBuilder();
    command.append(protocPath).append(" ");
    for (String importRoot : importRoots) command.append("-I=").append(importRoot).append(" ");
    command.append(langOutFlag(language)).append(outputFolderPath).append(" ");
    command.append(protoFile.getLocation().toOSString());
    return command.toString();
  }

  private String langOutFlag(SupportedLanguage targetLanguage) {
    return LANG_OUT_FLAG.get(targetLanguage);
  }
}
