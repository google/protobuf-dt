/*
 * Copyright (c) 2012 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.ui.builder.protoc.command;

import static com.google.common.collect.Lists.newArrayList;
import static java.util.Collections.unmodifiableList;

import java.util.List;

import org.eclipse.core.resources.*;
import org.eclipse.core.runtime.CoreException;

import com.google.eclipse.protobuf.ui.preferences.compiler.core.CompilerPreferences;
import com.google.eclipse.protobuf.ui.preferences.paths.core.PathsPreferences;

/**
 * Builds the command to call protoc to compile a single .proto file.
 *
 * @author alruiz@google.com (Alex Ruiz)
 */
public class ProtocCommandBuilder {
  private final List<ProtocOption> options = newArrayList();

  private final String protocPath;
  private final ImportRootsProtocOption importRootsProtocOption;

  public ProtocCommandBuilder(CompilerPreferences compilerPreferences, PathsPreferences pathsPreferences,
      IProject project) {
    boolean useProtocInSystemPath = compilerPreferences.useProtocInSystemPath().getValue();
    protocPath = useProtocInSystemPath ? "protoc" : compilerPreferences.protocPath().getValue();
    options.add(new DescriptorPathProtocOption(compilerPreferences));
    options.add(new JavaProtocOption(compilerPreferences, project));
    options.add(new CppProtocOption(compilerPreferences, project));
    options.add(new PythonProtocOption(compilerPreferences, project));
    importRootsProtocOption = new ImportRootsProtocOption(pathsPreferences, project);
  }

  /**
   * Builds the command to call protoc to compile a single .proto file.
   * @param protoFile the .proto file.
   * @return the built command.
   * @throws CoreException if something goes wrong.
   */
  public String buildCommand(IFile protoFile) throws CoreException {
    ProtocCommand command = new ProtocCommand(protocPath);
    importRootsProtocOption.appendOptionToCommand(command, protoFile);
    for (ProtocOption option : options) {
      option.appendOptionToCommand(command);
    }
    return command.toString();
  }

  /**
   * Returns the output directories where generated code is stored.
   * @return the output directories where generated code is stored.
   * @throws CoreException if something goes wrong.
   */
  public List<IFolder> outputDirectories() throws CoreException {
    List<IFolder> outputDirectories = newArrayList();
    for (ProtocOption option : options) {
      if (option instanceof OutputDirectoryProtocOption) {
        IFolder outputDirectory = ((OutputDirectoryProtocOption) option).outputDirectory();
        outputDirectories.add(outputDirectory);
      }
    }
    return unmodifiableList(outputDirectories);
  }
}
