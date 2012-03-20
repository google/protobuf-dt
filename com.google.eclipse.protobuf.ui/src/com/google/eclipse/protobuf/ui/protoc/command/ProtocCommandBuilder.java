/*
 * Copyright (c) 2012 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.ui.protoc.command;

import static com.google.common.collect.Lists.newArrayList;
import static java.util.Collections.unmodifiableList;

import java.util.List;

import org.eclipse.core.resources.*;
import org.eclipse.core.runtime.CoreException;

import com.google.eclipse.protobuf.ui.preferences.compiler.CompilerPreferences;
import com.google.eclipse.protobuf.ui.preferences.paths.PathsPreferences;

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
    protocPath = compilerPreferences.protocPath();
    options.add(new DescriptorPathProtocOption(compilerPreferences));
    options.add(new CodeGenerationProtocOption("java_out", compilerPreferences.javaCodeGeneration(), project));
    options.add(new CodeGenerationProtocOption("cpp_out", compilerPreferences.cppCodeGeneration(), project));
    options.add(new CodeGenerationProtocOption("python_out", compilerPreferences.pythonCodeGeneration(), project));
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
    importRootsProtocOption.addOptionToCommand(command, protoFile);
    for (ProtocOption option : options) {
      option.addOptionTo(command);
    }
    command.setFileToCompile(protoFile);
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
      if (option instanceof CodeGenerationProtocOption) {
        CodeGenerationProtocOption outputDirectoryProtocOption = (CodeGenerationProtocOption) option;
        IFolder outputDirectory = outputDirectoryProtocOption.outputDirectory();
        if (outputDirectory != null) {
          outputDirectories.add(outputDirectory);
        }
      }
    }
    return unmodifiableList(outputDirectories);
  }
}
