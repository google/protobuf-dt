/*
 * Copyright (c) 2011 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.ui.editor;

import static com.google.eclipse.protobuf.ui.util.Workbenches.activeWorkbenchPage;

import org.eclipse.core.filesystem.EFS;
import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.Path;
import org.eclipse.emf.common.util.URI;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.ide.FileStoreEditorInput;
import org.eclipse.ui.part.FileEditorInput;

import com.google.eclipse.protobuf.util.Uris;
import com.google.inject.Inject;
import com.google.inject.Singleton;

/**
 * Utility methods related to open file from different type of locations.
 *
 * @author alruiz@google.com (Alex Ruiz)
 */
@Singleton public class FileOpener {
  @Inject private Uris uris;

  public IEditorPart openProtoFileInWorkspace(URI uri) throws PartInitException {
    IWorkbenchPage page = activeWorkbenchPage();
    if (page == null) {
      return null;
    }
    IFile file = uris.referredFile(uri);
    IEditorInput editorInput = new FileEditorInput(file);
    return openFile(editorInput, page);
  }

  public IEditorPart openProtoFileInFileSystem(URI uri) throws PartInitException {
    IWorkbenchPage page = activeWorkbenchPage();
    if (page == null) {
      return null;
    }
    IFileStore fileStore = EFS.getLocalFileSystem().getStore(Path.fromOSString(uri.toFileString()));
    IEditorInput editorInput = new FileStoreEditorInput(fileStore);
    return openFile(editorInput, page);
  }

  public IEditorPart openProtoFileInPlugin(URI uri) throws PartInitException {
    IWorkbenchPage page = activeWorkbenchPage();
    if (page == null) {
      return null;
    }
    IEditorInput editorInput = new UriEditorInput(uri);
    return openFile(editorInput, page);
  }

  private IEditorPart openFile(IEditorInput editorInput, IWorkbenchPage page) throws PartInitException {
    return page.openEditor(editorInput, "com.google.eclipse.protobuf.Protobuf");
  }

}
