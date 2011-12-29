/*
 * Copyright (c) 2011 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.ui.builder.protoc;

import static com.google.eclipse.protobuf.util.CommonWords.space;
import static org.eclipse.xtext.util.Strings.isEmpty;

import java.util.List;

import org.eclipse.core.resources.*;

/**
 * @author alruiz@google.com (Alex Ruiz)
 */
class ProtocCommandFactory {
  String protocCommand(IFile protoFile, String protocPath, List<String> importRoots, String descriptorPath,
      OutputDirectories outputDirectories) {
    StringBuilder command = new StringBuilder();
    command.append(protocPath).append(space());
    for (String importRoot : importRoots) {
      command.append("-I=").append(importRoot).append(space());
    }
    if (!isEmpty(descriptorPath)) {
      command.append("--proto_path=").append(descriptorPath).append(space());
    }
    addOutputDirectory(outputDirectories.java(), "java", command);
    addOutputDirectory(outputDirectories.cpp(), "cpp", command);
    addOutputDirectory(outputDirectories.python(), "python", command);
    command.append(protoFile.getLocation().toOSString());
    return command.toString();
  }

  private void addOutputDirectory(OutputDirectory outputDirectory, String code, StringBuilder command) {
    if (!outputDirectory.isEnabled()) {
      return;
    }
    command.append("--").append(code).append("_out=");
    IFolder directory = outputDirectory.getLocation();
    command.append(directory.getLocation().toOSString()).append(space());
  }
}
