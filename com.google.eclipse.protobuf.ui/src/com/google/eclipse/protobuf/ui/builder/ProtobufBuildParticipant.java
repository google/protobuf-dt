/*
 * Copyright (c) 2011 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.ui.builder;

import static com.google.eclipse.protobuf.ui.preferences.compiler.PostCompilationRefreshTarget.PROJECT;
import static java.util.Collections.unmodifiableList;
import static org.eclipse.core.resources.IResource.DEPTH_INFINITE;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.core.filesystem.*;
import org.eclipse.core.resources.*;
import org.eclipse.core.runtime.*;
import org.eclipse.emf.common.util.URI;
import org.eclipse.xtext.builder.IXtextBuilderParticipant;
import org.eclipse.xtext.resource.IResourceDescription;
import org.eclipse.xtext.resource.IResourceDescription.Delta;

import com.google.eclipse.protobuf.ui.preferences.compiler.*;
import com.google.eclipse.protobuf.ui.preferences.paths.*;
import com.google.inject.Inject;

/**
 * Calls protoc to generate Java, C++ or Python code from .proto files.
 *
 * @author alruiz@google.com (Alex Ruiz)
 */
public class ProtobufBuildParticipant implements IXtextBuilderParticipant {

  private static final NullProgressMonitor NO_MONITOR = new NullProgressMonitor();

  private static Logger logger = Logger.getLogger(ProtobufBuildParticipant.class);

  @Inject private ProtocOutputParser outputParser;
  @Inject private ProtocCommandFactory commandFactory;
  @Inject private CompilerPreferenceReader compilerPreferenceReader;
  @Inject private PathsPreferenceReader pathsPreferenceReader;

  public void build(IBuildContext context, IProgressMonitor monitor) throws CoreException {
    IProject project = context.getBuiltProject();
    CompilerPreferences preferences = compilerPreferenceReader.readFromPrefereceStore(project);
    if (!preferences.compileProtoFiles) return;
    List<Delta> deltas = context.getDeltas();
    if (deltas.isEmpty()) return;
    IFolder outputFolder = findOrCreateOutputFolder(project, preferences.outputFolderName);
    List<String> importRoots = importRoots(project);
    for (Delta d : deltas) {
      IResourceDescription newResource = d.getNew();
      String path = filePathIfIsProtoFile(newResource);
      if (path == null) continue;
      IFile source = project.getWorkspace().getRoot().getFile(new Path(path));
      generateSingleProto(source, preferences.protocPath, importRoots, preferences.language, pathOf(outputFolder));
    }
    if (preferences.refreshResources) refresh(outputFolder, preferences.refreshTarget, monitor);
  }

  private static IFolder findOrCreateOutputFolder(IProject project, String outputFolderName) throws CoreException {
    IFolder outputFolder = project.getFolder(outputFolderName);
    if (!outputFolder.exists()) outputFolder.create(true, true, NO_MONITOR);
    return outputFolder;
  }

  private List<String> importRoots(IProject project) {
    List<String> paths = new ArrayList<String>();
    PathsPreferences preferences = pathsPreferenceReader.readFromPrefereceStore(project);
    List<DirectoryPath> directoryPaths = preferences.directoryPaths();
    for (DirectoryPath path : directoryPaths) {
      String location = locationOfDirectory(path, project);
      if (location != null) paths.add(location);
    }
    return unmodifiableList(paths);
  }

  private String locationOfDirectory(DirectoryPath path, IProject project) {
    if (path.isWorkspacePath()) return locationOfWorkspaceDirectory(path, project);
    return locationOfFileSystemDirectory(path);
  }

  private String locationOfWorkspaceDirectory(DirectoryPath path, IProject project) {
    IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
    IFolder folder = root.getFolder(new Path(path.value()));
    return pathOf(folder);
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
    if (!uri.fileExtension().equals("proto")) return null;
    if (uri.scheme() == null) return uri.toFileString();
    StringBuilder b = new StringBuilder();
    int segmentCount = uri.segmentCount();
    for (int i = 1; i < segmentCount; i++)
      b.append("/").append(uri.segment(i));
    return b.length() == 0 ? null : b.toString();
  }

  private void generateSingleProto(IFile source, String protocPath, List<String> importRoots,
      CompilerTargetLanguage language, String outputFolderPath) {
    String command = commandFactory.protocCommand(source, protocPath, importRoots, language, outputFolderPath);
    System.out.println(command);
    try {
      Process process = Runtime.getRuntime().exec(command);
      processStream(process.getErrorStream(), source);
      process.destroy();
    } catch (Exception ex) {
      // TODO show error message
      ex.printStackTrace();
    }
  }

  private void processStream(InputStream stream, IFile source) {
    InputStreamReader reader = null;
    try {
      reader = new InputStreamReader(stream);
      BufferedReader bufferedReader = new BufferedReader(reader);
      String line = null;
      ProtocMarkerFactory markerFactory = new ProtocMarkerFactory(source);
      while ((line = bufferedReader.readLine()) != null) {
        outputParser.parseAndAddMarkerIfNecessary(line, markerFactory);
        System.out.println("[protoc] " + line);
      }
    } catch (Exception e) {
      logger.fatal("Execution of protoc on [" + source.getName() + "] failed", e);
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

  private static String pathOf(IResource r) {
    return r.getLocation().toOSString();
  }

  private static void refresh(IFolder outputFolder, PostCompilationRefreshTarget refreshTarget, IProgressMonitor monitor)
      throws CoreException {
    IResource target = refreshTarget(outputFolder, refreshTarget);
    target.refreshLocal(DEPTH_INFINITE, monitor);
  }

  private static IResource refreshTarget(IFolder outputFolder, PostCompilationRefreshTarget refreshTarget) {
    if (refreshTarget.equals(PROJECT)) return outputFolder.getProject();
    return outputFolder;
  }
}
