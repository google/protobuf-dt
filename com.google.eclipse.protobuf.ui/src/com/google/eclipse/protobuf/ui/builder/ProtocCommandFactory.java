/*
 * Copyright (c) 2011 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.ui.builder;

import static com.google.eclipse.protobuf.util.CommonWords.space;

import com.google.eclipse.protobuf.ui.preferences.pages.compiler.SupportedLanguage;

import org.eclipse.core.resources.*;

import java.util.*;

/**
 * @author alruiz@google.com (Alex Ruiz)
 */
class ProtocCommandFactory {

  private static final Map<SupportedLanguage, String> LANG_OUT_FLAG = new HashMap<SupportedLanguage, String>();

  static {
    for (SupportedLanguage lang : SupportedLanguage.values())
      LANG_OUT_FLAG.put(lang, "--" + lang.code() + "_out=");
  }

  String protocCommand(IFile protoFile, String protocPath, List<String> importRoots,
      OutputDirectories outputDirectories) {
    StringBuilder command = new StringBuilder();
    command.append(protocPath).append(space());
    for (String importRoot : importRoots) {
      command.append("-I=").append(importRoot).append(space());
    }
    for (SupportedLanguage language : SupportedLanguage.values()) {
      IFolder outputDirectory = outputDirectories.outputDirectoryFor(language);
      if (outputDirectory == null) continue;
      command.append(langOutFlag(language)).append(outputDirectory.getLocation().toOSString()).append(space());
    }
    command.append(protoFile.getLocation().toOSString());
    return command.toString();
  }

  private String langOutFlag(SupportedLanguage targetLanguage) {
    return LANG_OUT_FLAG.get(targetLanguage);
  }
}
