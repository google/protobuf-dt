/*
 * Copyright (c) 2011 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.ui.builder;

import static com.google.eclipse.protobuf.ui.builder.OutputDirectories.findOrCreateOutputDirectories;
import static com.google.eclipse.protobuf.ui.exception.CoreExceptions.error;
import static com.google.eclipse.protobuf.ui.preferences.pages.compiler.PostCompilationRefreshTarget.PROJECT;
import static com.google.eclipse.protobuf.ui.preferences.pages.paths.PathResolutionType.MULTIPLE_DIRECTORIES;
import static java.util.Collections.*;
import static org.eclipse.core.resources.IResource.DEPTH_INFINITE;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.filesystem.*;
import org.eclipse.core.resources.*;
import org.eclipse.core.runtime.*;
import org.eclipse.emf.common.util.URI;
import org.eclipse.xtext.builder.IXtextBuilderParticipant;
import org.eclipse.xtext.resource.IResourceDescription;
import org.eclipse.xtext.resource.IResourceDescription.Delta;

import com.google.eclipse.protobuf.ui.preferences.pages.compiler.*;
import com.google.eclipse.protobuf.ui.preferences.pages.paths.*;
import com.google.inject.Inject;

/**
 * Calls protoc to generate Java, C++ or Python code from .proto files.
 *
 * @author alruiz@google.com (Alex Ruiz)
 */
public class ProtobufBuildParticipant implements IXtextBuilderParticipant {

  @Inject private ProtocOutputParser outputParser;
  @Inject private ProtocCommandFactory commandFactory;
  @Inject private CompilerPreferencesFactory compilerPreferencesFactory;
  @Inject private PathsPreferencesFactory pathsPreferencesFactory;

  public void build(IBuildContext context, IProgressMonitor monitor) throws CoreException {
    IProject project = context.getBuiltProject();
    CompilerPreferences preferences = compilerPreferencesFactory.preferences(project);
    if (!preferences.shouldCompileProtoFiles()) return;
    List<Delta> deltas = context.getDeltas();
    if (deltas.isEmpty()) return;
    OutputDirectories outputDirectories = findOrCreateOutputDirectories(project, preferences.codeGenerationSettings());
    List<String> importRoots = importRoots(project);
    for (Delta d : deltas) {
      IResourceDescription newResource = d.getNew();
      String path = filePathIfIsProtoFile(newResource);
      if (path == null) continue;
      IFile source = project.getWorkspace().getRoot().getFile(new Path(path));
      if (importRoots.isEmpty()) importRoots = singleImportRoot(source);
      generateSingleProto(source, preferences.protocPath(), importRoots, outputDirectories);
    }
    if (preferences.shouldRefreshResources()) refresh(project, outputDirectories, preferences.refreshTarget(), monitor);
  }

  private List<String> importRoots(IProject project) {
    List<String> paths = new ArrayList<String>();
    PathsPreferences preferences = pathsPreferencesFactory.preferences(project);
    if (MULTIPLE_DIRECTORIES.equals(preferences.pathResolutionType())) {
      List<DirectoryPath> directoryPaths = preferences.importRoots();
      for (DirectoryPath path : directoryPaths) {
        String location = locationOfDirectory(path, project);
        if (location != null) paths.add(location);
      }
      return unmodifiableList(paths);
    }
    return emptyList();
  }

  private String locationOfDirectory(DirectoryPath path, IProject project) {
    if (path.isWorkspacePath()) return locationOfWorkspaceDirectory(path, project);
    return locationOfFileSystemDirectory(path);
  }

  private String locationOfWorkspaceDirectory(DirectoryPath path, IProject project) {
    IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
    IFolder folder = root.getFolder(new Path(path.value()));
    return folder.getLocation().toOSString();
  }

  private String locationOfFileSystemDirectory(DirectoryPath path) {
    IFileSystem fileSystem = EFS.getLocalFileSystem();
    IFileInfo fileInfo = fileSystem.getStore(new Path(path.value())).fetchInfo();
    if (!fileInfo.isDirectory()) return null;
    return path.value();
  }

  private static String filePathIfIsProtoFile(IResourceDescription r) {
    if (r == null) return null;
    URI uri = r.getURI();
    if (!uri.fileExtension().equals("proto")) return null; //$NON-NLS-1$
    if (uri.scheme() == null) return uri.toFileString();
    StringBuilder b = new StringBuilder();
    int segmentCount = uri.segmentCount();
    for (int i = 1; i < segmentCount; i++)
      b.append("/").append(uri.segment(i)); //$NON-NLS-1$
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

  private void generateSingleProto(IFile source, String protocPath, List<String> importRoots,
      OutputDirectories outputDirectories) throws CoreException {
    String command = commandFactory.protocCommand(source, protocPath, importRoots, outputDirectories);
    System.out.println(command);
    try {
      Process process = Runtime.getRuntime().exec(command);
      processStream(process.getErrorStream(), source);
      process.destroy();
    } catch (Throwable e) {
      throw error(e);
    }
  }

  private void processStream(InputStream stream, IFile source) throws Throwable {
    InputStreamReader reader = null;
    try {
      reader = new InputStreamReader(stream);
      BufferedReader bufferedReader = new BufferedReader(reader);
      String line = null;
      ProtocMarkerFactory markerFactory = new ProtocMarkerFactory(source);
      while ((line = bufferedReader.readLine()) != null) {
        outputParser.parseAndAddMarkerIfNecessary(line, markerFactory);
        System.out.println("[protoc] " + line); //$NON-NLS-1$
      }
    } finally {
      close(reader);
    }
  }

  private static void close(Reader reader) {
    if (reader == null) return;
    try {
      reader.close();
    } catch (IOException ignored) {}
  }

  private static void refresh(IProject project, OutputDirectories outputDirectories,
      PostCompilationRefreshTarget refreshTarget, IProgressMonitor monitor) throws CoreException {
    if (refreshTarget.equals(PROJECT)) {
      refresh(project, monitor);
      return;
    }
    for (IFolder outputDirectory : outputDirectories.values()) refresh(outputDirectory, monitor);
  }

  private static void refresh(IResource target, IProgressMonitor monitor) throws CoreException {
    target.refreshLocal(DEPTH_INFINITE, monitor);
  }
}
