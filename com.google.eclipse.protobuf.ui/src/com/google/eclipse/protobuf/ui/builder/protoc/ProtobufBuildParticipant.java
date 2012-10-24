/*
 * Copyright (c) 2011 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.ui.builder.protoc;

import static org.eclipse.core.resources.IResource.DEPTH_INFINITE;

import static com.google.common.io.Closeables.closeQuietly;
import static com.google.eclipse.protobuf.ui.builder.protoc.ConsolePrinter.createAndDisplayConsole;
import static com.google.eclipse.protobuf.ui.preferences.compiler.CompilerPreferences.compilerPreferences;
import static com.google.eclipse.protobuf.ui.util.IStatusFactory.error;
import static com.google.eclipse.protobuf.util.Strings.quote;
import static com.google.eclipse.protobuf.util.Workspaces.workspaceRoot;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.SubMonitor;
import org.eclipse.emf.common.util.URI;
import org.eclipse.xtext.builder.IXtextBuilderParticipant;
import org.eclipse.xtext.resource.IResourceDescription.Delta;
import org.eclipse.xtext.ui.editor.preferences.IPreferenceStoreAccess;

import com.google.eclipse.protobuf.ui.preferences.compiler.CompilerPreferences;
import com.google.eclipse.protobuf.ui.preferences.paths.PathsPreferences;
import com.google.eclipse.protobuf.ui.protoc.command.ProtocCommandBuilder;
import com.google.eclipse.protobuf.ui.protoc.output.ProtocMarkerFactory;
import com.google.eclipse.protobuf.ui.protoc.output.ProtocOutputParser;
import com.google.eclipse.protobuf.util.Uris;
import com.google.inject.Inject;

/**
 * Calls protoc to generate Java, C++ or Python code from .proto files.
 *
 * @author alruiz@google.com (Alex Ruiz)
 */
public class ProtobufBuildParticipant implements IXtextBuilderParticipant {
  @Inject private ProtocOutputParser outputParser;
  @Inject private IPreferenceStoreAccess storeAccess;
  @Inject private Uris uris;

  @Override public void build(IBuildContext context, IProgressMonitor monitor) throws CoreException {
    List<Delta> deltas = context.getDeltas();
    if (deltas.isEmpty()) {
      return;
    }
    if (monitor.isCanceled()) {
      throw new OperationCanceledException();
    }
    SubMonitor subMonitor = SubMonitor.convert(monitor, deltas.size() * 2 + 2);
    IProject project = context.getBuiltProject();
    CompilerPreferences compilerPreferences = compilerPreferences(storeAccess, project);
    if (!compilerPreferences.shouldCompileProtoFiles()) {
      return;
    }
    PathsPreferences pathsPreferences = new PathsPreferences(storeAccess, project);
    ProtocCommandBuilder commandBuilder = new ProtocCommandBuilder(compilerPreferences, pathsPreferences);
    for (Delta delta : deltas) {
      if (subMonitor.isCanceled()) {
        throw new OperationCanceledException();
      }
      IFile protoFile = protoFile(delta.getUri(), project);
      subMonitor.worked(1);
      if (protoFile != null) {
        subMonitor.subTask("Compiling " + protoFile.getName() + " with protoc");
        generateSingleProto(commandBuilder.buildCommand(protoFile), protoFile);
      }
      subMonitor.worked(1);
    }
    if (compilerPreferences.refreshResources()) {
      List<IFolder> outputDirectories = commandBuilder.outputDirectories();
      boolean refreshProject = compilerPreferences.refreshProject();
      refresh(project, outputDirectories, refreshProject, subMonitor.newChild(outputDirectories.size() + 1));
    }
  }

  private IFile protoFile(URI resourceUri, IProject project) {
    String path = filePathIfIsProtoFile(resourceUri);
    return (path == null) ? null : workspaceRoot().getFile(Path.fromOSString(path));
  }

  private String filePathIfIsProtoFile(URI resourceUri) {
    if (uris.hasProtoExtension(resourceUri) && resourceUri.isPlatformResource()) {
      return resourceUri.toPlatformString(true);
    }
    return null;
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
      throw new CoreException(error(e));
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
      monitor.subTask("Refreshing project " + quote(project.getName()));
      project.refreshLocal(DEPTH_INFINITE, monitor);
      monitor.worked(1);
      return;
    }
    for (IFolder outputDirectory : outputDirectories) {
      monitor.subTask("Refreshing folder " + quote(outputDirectory.getName()));
      outputDirectory.refreshLocal(DEPTH_INFINITE, monitor);
      monitor.worked(1);
    }
  }
}
