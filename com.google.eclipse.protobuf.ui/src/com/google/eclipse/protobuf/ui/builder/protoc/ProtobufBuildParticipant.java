/*
 * Copyright (c) 2011 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.ui.builder.protoc;

import static com.google.common.io.Closeables.closeQuietly;
import static com.google.eclipse.protobuf.ui.builder.protoc.ConsolePrinter.createAndDisplayConsole;
import static com.google.eclipse.protobuf.ui.exception.CoreExceptions.error;
import static com.google.eclipse.protobuf.ui.preferences.compiler.CompilerPreferences.compilerPreferences;
import static com.google.eclipse.protobuf.ui.util.Workspaces.workspaceRoot;
import static org.eclipse.core.resources.IResource.DEPTH_INFINITE;

import java.io.*;
import java.util.List;

import org.eclipse.core.resources.*;
import org.eclipse.core.runtime.*;
import org.eclipse.emf.common.util.URI;
import org.eclipse.xtext.builder.IXtextBuilderParticipant;
import org.eclipse.xtext.resource.*;
import org.eclipse.xtext.resource.IResourceDescription.Delta;
import org.eclipse.xtext.ui.editor.preferences.IPreferenceStoreAccess;

import com.google.eclipse.protobuf.ui.preferences.compiler.CompilerPreferences;
import com.google.eclipse.protobuf.ui.preferences.paths.PathsPreferences;
import com.google.eclipse.protobuf.ui.protoc.command.ProtocCommandBuilder;
import com.google.eclipse.protobuf.ui.protoc.output.*;
import com.google.inject.Inject;

/**
 * Calls protoc to generate Java, C++ or Python code from .proto files.
 *
 * @author alruiz@google.com (Alex Ruiz)
 */
public class ProtobufBuildParticipant implements IXtextBuilderParticipant {
  @Inject private ProtocOutputParser outputParser;
  @Inject private IPreferenceStoreAccess storeAccess;

  @Override public void build(IBuildContext context, IProgressMonitor monitor) throws CoreException {
    List<Delta> deltas = context.getDeltas();
    if (deltas.isEmpty()) {
      return;
    }
    IProject project = context.getBuiltProject();
    CompilerPreferences compilerPreferences = compilerPreferences(storeAccess, project);
    if (!compilerPreferences.shouldCompileProtoFiles()) {
      return;
    }
    PathsPreferences pathsPreferences = new PathsPreferences(storeAccess, project);
    ProtocCommandBuilder commandBuilder = new ProtocCommandBuilder(compilerPreferences, pathsPreferences);
    for (Delta d : deltas) {
      IFile protoFile = protoFile(d.getNew(), project);
      if (protoFile == null) {
        continue;
      }
      generateSingleProto(commandBuilder.buildCommand(protoFile), protoFile);
    }
    if (compilerPreferences.refreshResources()) {
      refresh(project, commandBuilder.outputDirectories(), compilerPreferences.refreshProject(), monitor);
    }
  }

  private IFile protoFile(IResourceDescription resource, IProject project) {
    String path = filePathIfIsProtoFile(resource);
    return (path == null) ? null : workspaceRoot().getFile(new Path(path));
  }

  private String filePathIfIsProtoFile(IResourceDescription resource) {
    if (resource == null) {
      return null;
    }
    URI uri = resource.getURI();
    if (!uri.isPlatformResource() && !uri.fileExtension().equals("proto")) {
      return null;
    }
    return uri.toPlatformString(true);
  }

  private void generateSingleProto(String command, IFile protoFile) throws CoreException {
    ConsolePrinter console = null;
    try {
      console = createAndDisplayConsole();
      console.printCommand(command);
      Process process = Runtime.getRuntime().exec(command);
      processStream(process.getErrorStream(), protoFile, console);
      process.destroy();
    } catch (Throwable e) {
      e.printStackTrace();
      throw error(e);
    } finally {
      if (console != null) {
        console.close();
      }
    }
  }

  private void processStream(InputStream stream, IFile protoFile, ConsolePrinter console) throws Throwable {
    InputStreamReader reader = null;
    try {
      reader = new InputStreamReader(stream);
      BufferedReader bufferedReader = new BufferedReader(reader);
      String line = null;
      ProtocMarkerFactory markerFactory = new ProtocMarkerFactory(protoFile);
      while ((line = bufferedReader.readLine()) != null) {
        outputParser.parseAndAddMarkerIfNecessary(line, markerFactory);
        console.printProtocOutput(line);
      }
    } finally {
      closeQuietly(reader);
    }
  }

  private void refresh(IProject project, List<IFolder> outputDirectories, boolean refreshProject,
      IProgressMonitor monitor) throws CoreException {
    if (refreshProject) {
      project.refreshLocal(DEPTH_INFINITE, monitor);
      return;
    }
    for (IFolder outputDirectory : outputDirectories) {
      outputDirectory.refreshLocal(DEPTH_INFINITE, monitor);
    }
  }
}
