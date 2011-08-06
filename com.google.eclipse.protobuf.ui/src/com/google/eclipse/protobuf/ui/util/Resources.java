/*
 * Copyright (c) 2011 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.ui.util;

import org.eclipse.core.filesystem.EFS;
import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.core.resources.*;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.emf.common.util.URI;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.ui.*;
import org.eclipse.ui.ide.FileStoreEditorInput;
import org.eclipse.ui.internal.editors.text.EditorsPlugin;
import org.eclipse.ui.internal.editors.text.NonExistingFileEditorInput;
import org.eclipse.ui.part.FileEditorInput;
import org.eclipse.ui.views.navigator.ResourceNavigator;

import com.google.inject.Singleton;

/**
 * Utility methods related to resources (e.g. files, directories.)
 *
 * @author alruiz@google.com (Alex Ruiz)
 */
@Singleton
public class Resources {

  public static final String URI_SCHEME_FOR_FILES = "file";

  /**
   * Returns the project that contains the resource at the given URI.
   * @param resourceUri the given URI.
   * @return the project that contains the resource at the given URI, or {@code null} if the resource at the given URI
   * is not in a workspace.
   */
  public IProject project(URI resourceUri) {
    IFile file = file(resourceUri);
    return (file != null) ? file.getProject() : null;
  }

  public IProject activeProject() {
    IViewReference[] references = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getViewReferences();
    for (IViewReference reference : references) {
      IViewPart part = reference.getView(false);
      if (!(part instanceof ResourceNavigator)) continue;
      ResourceNavigator navigator = (ResourceNavigator) part;
      StructuredSelection selection = (StructuredSelection) navigator.getTreeViewer().getSelection();
      IResource resource = (IResource) selection.getFirstElement();
      return resource.getProject();
    }
    return null;
  }

  /**
   * Indicates whether the given URI belongs to an existing file.
   * @param fileUri the URI to check, as a {@code String}.
   * @return {@code true} if the given URI belongs to an existing file, {@code false} otherwise.
   */
  public boolean fileExists(URI fileUri) {
    IFile file = file(fileUri);
    return (file != null) ? file.exists() : false;
  }

  /**
   * Opens the .proto file identified by the given URI that exists in the workspace.
   * @param uri the URI of the file to open.
   * @return an open and active editor, or {@code null} if an external editor was opened.
   * @throws PartInitException if the editor cannot be opened or initialized.
   */
  public IEditorPart openProtoFileInWorkspace(URI uri) throws PartInitException {
    IFile file = file(uri);
    IEditorInput editorInput = new FileEditorInput(file);
    return openFile(editorInput);
  }

  /**
   * Opens the .proto file identified by the given URI that does not exist in the workspace, therefore is
   * opened from the file system.
   * @param uri the URI of the file to open.
   * @return an open and active editor, or {@code null} if an external editor was opened.
   * @throws PartInitException if the editor cannot be created or initialized.
   */
  public IEditorPart openProtoFileInFileSystem(URI uri) throws PartInitException {
    IFileStore fileStore = EFS.getLocalFileSystem().getStore(new Path(uri.toFileString()));
    IEditorInput editorInput = new FileStoreEditorInput(fileStore);
    return openFile(editorInput/*"org.eclipse.ui.DefaultTextEditor"*/);
  }

  @SuppressWarnings("restriction")
  public IEditorPart openProtoFileInPlugin(URI uri) throws PartInitException {
    IFileStore fileStore = queryFileStore();
    IEditorInput editorInput = new NonExistingFileEditorInput(fileStore, "descriptor.proto");
    return openFile(editorInput);
  }

  @SuppressWarnings("restriction")
  private IFileStore queryFileStore() {
    IPath stateLocation = EditorsPlugin.getDefault().getStateLocation();
    IPath path = stateLocation.append("/_" + new Object().hashCode()); //$NON-NLS-1$
    return EFS.getLocalFileSystem().getStore(path);
  }

  private IEditorPart openFile(IEditorInput editorInput) throws PartInitException {
    IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
    return page.openEditor(editorInput, "com.google.eclipse.protobuf.Protobuf");
  }

  /**
   * Returns a handle to a workspace file identified by the given URI.
   * @param uri the given URI.
   * @return a handle to a workspace file identified by the given URI or {@code null} if the URI does not belong to a
   * workspace file.
   */
  public IFile file(URI uri) {
    IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
    IPath path = pathOf(uri);
    return (path != null) ? root.getFile(path) : null;
  }

  /**
   * Returns the project owning the file displayed in the given editor.
   * @param editor the given editor.
   * @return the project owning the file displayed in the given editor.
   */
  public IProject project(IEditorPart editor) {
    IResource resource = resourceFrom(editor);
    return (resource == null) ? null : resource.getProject();
  }

  private IResource resourceFrom(IEditorPart editor) {
    if (editor == null) return null;
    Object adapter = editor.getEditorInput().getAdapter(IResource.class);
    return (adapter == null) ? null : (IResource) adapter;
  }

  private IPath pathOf(URI uri) {
    String platformUri = uri.toPlatformString(true);
    return (platformUri != null) ? new Path(platformUri) : null;
  }
}
