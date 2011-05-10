/*
 * Copyright (c) 2011 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.ui.builder;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IPath;

import com.google.eclipse.protobuf.ui.preferences.compiler.TargetLanguage;

/**
 * @author alruiz@google.com (Alex Ruiz)
 */
class ProtocCommandFactory {
  private static final Map<TargetLanguage, String> LANG_OUT_FLAG = new HashMap<TargetLanguage, String>();

  static {
    for (TargetLanguage lang : TargetLanguage.values())
      LANG_OUT_FLAG.put(lang, "--" + lang.name().toLowerCase() + "_out=");
  }

  String protocCommand(IFile protoFile, String protocPath, TargetLanguage language, String outputFolderPath) {
    IPath protoFilePath = protoFile.getLocation();
    StringBuilder command = new StringBuilder();
    command.append(protocPath).append(" ");
    String protoFileFolder = protoFilePath.toFile().getParentFile().toString();
    command.append("-I=").append(protoFileFolder).append(" ");
    command.append(langOutFlag(language)).append(outputFolderPath).append(" ");
    command.append(protoFilePath.toOSString());
    return command.toString();
  }

  private String langOutFlag(TargetLanguage targetLanguage) {
    return LANG_OUT_FLAG.get(targetLanguage);
  }
}
