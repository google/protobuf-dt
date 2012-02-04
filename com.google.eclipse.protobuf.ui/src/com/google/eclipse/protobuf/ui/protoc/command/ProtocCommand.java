/*
 * Copyright (c) 2012 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.ui.protoc.command;

import static com.google.eclipse.protobuf.util.CommonWords.space;

import org.eclipse.core.resources.IFile;

/**
 * The command used to call protoc to compile a single .proto file.
 *
 * @author alruiz@google.com (Alex Ruiz)
 */
class ProtocCommand {
  private final StringBuilder content = new StringBuilder();

  ProtocCommand(String protocPath) {
    content.append(protocPath).append(space());
  }

  /**
   * Appends the given option name and value to the command. So far, the best description of protoc options is
   * <a href="http://www.discursive.com/books/cjcook/reference/proto-sect-compiling"> this one</a>.
   * @param name the given option name.
   * @param value the given option value.
   */
  void appendOption(String name, String value) {
    content.append("--").append(name).append("=").append(value).append(space());
  }

  @Override public String toString() {
    return content.toString().trim();
  }

  void setFileToCompile(IFile protoFile) {
    content.append(protoFile.getLocation().toOSString());
  };
}
