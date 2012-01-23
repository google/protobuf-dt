/*
 * Copyright (c) 2011 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.ui.builder.protoc;

import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.io.Closeables.closeQuietly;
import static com.google.eclipse.protobuf.ui.builder.protoc.ConsolePrinter.createAndDisplayConsole;
import static com.google.eclipse.protobuf.ui.exception.CoreExceptions.error;
import static com.google.eclipse.protobuf.ui.preferences.compiler.core.CompilerPreferences.compilerPreferences;
import static com.google.eclipse.protobuf.ui.util.CommaSeparatedValues.splitCsv;
import static java.util.Collections.*;
import static org.eclipse.core.resources.IResource.DEPTH_INFINITE;

import java.io.*;
import java.util.List;

import org.eclipse.core.resources.*;
import org.eclipse.core.runtime.*;
import org.eclipse.emf.common.util.URI;
import org.eclipse.xtext.builder.IXtextBuilderParticipant;
import org.eclipse.xtext.resource.IResourceDescription;
import org.eclipse.xtext.resource.IResourceDescription.Delta;
import org.eclipse.xtext.ui.editor.preferences.IPreferenceStoreAccess;

import com.google.eclipse.protobuf.ui.preferences.compiler.core.CompilerPreferences;
import com.google.eclipse.protobuf.ui.preferences.paths.core.DirectoryPath;
import com.google.eclipse.protobuf.ui.preferences.paths.core.PathsPreferences;
import com.google.inject.Inject;

/**
 * Calls protoc to generate Java, C++ or Python code from .proto files.
 *
 * @author alruiz@google.com (Alex Ruiz)
 */
public class ProtobufBuildParticipant implements IXtextBuilderParticipant {
  @Inject private ProtocCommandFactory commandFactory;
  @Inject private ProtocOutputParser outputParser;
  @Inject private ProtoDescriptorPathFinder protoDescriptorPathFinder;
  @Inject private IPreferenceStoreAccess storeAccess;

  @Override public void build(IBuildContext context, IProgressMonitor monitor) throws CoreException {
    IProject project = context.getBuiltProject();
    CompilerPreferences preferences = compilerPreferences(storeAccess, project);
    if (!preferences.compileProtoFiles().getValue()) {
      return;
    }
    List<Delta> deltas = context.getDeltas();
    if (deltas.isEmpty()) {
      return;
    }
    OutputDirectories outputDirectories = new OutputDirectories(project, preferences);
    String descriptorPath = descriptorPath(preferences);
    List<String> importRoots = importRoots(project);
    for (Delta d : deltas) {
      IFile source = protoFile(d.getNew(), project);
      if (source == null) {
        continue;
      }
      if (importRoots.isEmpty()) {
        importRoots = singleImportRoot(source);
      }
      generateSingleProto(source, protocPath(preferences), importRoots, descriptorPath, outputDirectories);
    }
    if (preferences.refreshResources().getValue()) {
      boolean refreshProject = preferences.refreshProject().getValue();
      refresh(project, outputDirectories, refreshProject, monitor);
    }
  }

  private String descriptorPath(CompilerPreferences preferences) {
    return protoDescriptorPathFinder.findRootOf(preferences.descriptorPath().getValue());
  }

  private List<String> importRoots(IProject project) {
    List<String> paths = newArrayList();
    PathsPreferences preferences = new PathsPreferences(storeAccess, project);
    if (preferences.filesInMultipleDirectories().getValue()) {
      String directoryPaths = preferences.directoryPaths().getValue();
      for (String importRoot : splitCsv(directoryPaths)) {
        DirectoryPath path = DirectoryPath.parse(importRoot, project);
        String location = path.absolutePathInFileSystem();
        if (location != null) {
          paths.add(location);
        }
      }
      return unmodifiableList(paths);
    }
    return emptyList();
  }

  private IFile protoFile(IResourceDescription resource, IProject project) {
    String path = filePathIfIsProtoFile(resource);
    return (path == null) ? null : project.getWorkspace().getRoot().getFile(new Path(path));
  }

  private String filePathIfIsProtoFile(IResourceDescription resource) {
    if (resource == null) {
      return null;
    }
    URI uri = resource.getURI();
    if (!uri.fileExtension().equals("proto"))
    {
      return null;
    }
    if (uri.scheme() == null) {
      return uri.toFileString();
    }
    StringBuilder b = new StringBuilder();
    int segmentCount = uri.segmentCount();
    for (int i = 1; i < segmentCount; i++)
     {
      b.append("/").append(uri.segment(i));
    }
    return b.length() == 0 ? null : b.toString();
  }

  private List<String> singleImportRoot(IFile source) {
    IProject project = source.getProject();
    File projectFile = project.getLocation().toFile();
    File current = source.getLocation().toFile();
    while (!current.getParentFile().equals(projectFile)) {
      current = current.getParentFile();
    }
    return singletonList(current.toString());
  }

  String protocPath(CompilerPreferences preferences) {
    if (preferences.useProtocInSystemPath().getValue()) {
      return "protoc";
    }
    return preferences.protocPath().getValue();
  }

  private void generateSingleProto(IFile source, String protocPath, List<String> importRoots, String descriptorPath,
      OutputDirectories outputDirectories) throws CoreException {
    String command = commandFactory.protocCommand(source, protocPath, importRoots, descriptorPath, outputDirectories);
    ConsolePrinter console = null;
    try {
      console = createAndDisplayConsole();
      console.println("[command] " + command);
      Process process = Runtime.getRuntime().exec(command);
      processStream(process.getErrorStream(), source, console);
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

  private void processStream(InputStream stream, IFile source, ConsolePrinter console) throws Throwable {
    InputStreamReader reader = null;
    try {
      reader = new InputStreamReader(stream);
      BufferedReader bufferedReader = new BufferedReader(reader);
      String line = null;
      ProtocMarkerFactory markerFactory = new ProtocMarkerFactory(source);
      while ((line = bufferedReader.readLine()) != null) {
        outputParser.parseAndAddMarkerIfNecessary(line, markerFactory);
        console.println("[protoc]  " + line);
      }
    } finally {
      closeQuietly(reader);
    }
  }

  private void refresh(IProject project, OutputDirectories outputDirectories, boolean refreshProject,
      IProgressMonitor monitor) throws CoreException {
    if (refreshProject) {
      project.refreshLocal(DEPTH_INFINITE, monitor);
      return;
    }
    refresh(outputDirectories.java(), monitor);
    refresh(outputDirectories.cpp(), monitor);
    refresh(outputDirectories.python(), monitor);
  }

  private void refresh(OutputDirectory directory, IProgressMonitor monitor) throws CoreException {
    if (directory.isEnabled()) {
      IFolder location = directory.getLocation();
      location.refreshLocal(DEPTH_INFINITE, monitor);
    }
  }
}
