/*
 * Copyright (c) 2011 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.ui.editor;

import org.eclipse.core.filesystem.EFS;
import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.Path;
import org.eclipse.emf.common.util.URI;
import org.eclipse.ui.*;
import org.eclipse.ui.ide.FileStoreEditorInput;
import org.eclipse.ui.part.FileEditorInput;

import com.google.eclipse.protobuf.ui.util.Resources;
import com.google.inject.Inject;
import com.google.inject.Singleton;

/**
 * Utility methods related to open file from different type of locations.
 *
 * @author alruiz@google.com (Alex Ruiz)
 */
@Singleton
public class FileOpener {

  @Inject private Resources resources;

  public IEditorPart openProtoFileInWorkspace(URI uri) throws PartInitException {
    IFile file = resources.file(uri);
    IEditorInput editorInput = new FileEditorInput(file);
    return openFile(editorInput);
  }

  public IEditorPart openProtoFileInFileSystem(URI uri) throws PartInitException {
    IFileStore fileStore = EFS.getLocalFileSystem().getStore(new Path(uri.toFileString()));
    IEditorInput editorInput = new FileStoreEditorInput(fileStore);
    return openFile(editorInput/*"org.eclipse.ui.DefaultTextEditor"*/);
  }

  public IEditorPart openProtoFileInPlugin(URI uri) throws PartInitException {
    IEditorInput editorInput = new UriEditorInput(uri);
    return openFile(editorInput);
  }

  private IEditorPart openFile(IEditorInput editorInput) throws PartInitException {
    IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
    return page.openEditor(editorInput, "com.google.eclipse.protobuf.Protobuf");
  }

}
