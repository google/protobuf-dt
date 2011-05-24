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
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.hyperlink.IHyperlink;
import org.eclipse.ui.*;
import org.eclipse.ui.ide.FileStoreEditorInput;
import org.eclipse.ui.part.FileEditorInput;

import com.google.eclipse.protobuf.ui.util.Resources;

/**
 * A hyperlink for imported .proto files.
 *
 * @author alruiz@google.com (Alex Ruiz)
 */
class ImportHyperlink implements IHyperlink {

  private final URI importUri;
  private final IRegion region;
  private final Resources resources;

  ImportHyperlink(URI importUri, IRegion region, Resources resources) {
    this.importUri = importUri;
    this.region = region;
    this.resources = resources;
  }

  public void open() {
    String scheme = importUri.scheme();
    if ("platform".equals(scheme)) openFromWorkspace();
    if ("file".equals(scheme)) openFromFileSystem();
  }

  private void openFromWorkspace() {
    IFile file = resources.file(importUri);
    IEditorInput editorInput = new FileEditorInput(file);
    openFile(editorInput, "com.google.eclipse.protobuf.Protobuf");
  }

  private void openFromFileSystem() {
    IFileStore fileStore = EFS.getLocalFileSystem().getStore(new Path(importUri.toFileString()));
    IEditorInput editorInput = new FileStoreEditorInput(fileStore);
    openFile(editorInput, "org.eclipse.ui.DefaultTextEditor");
  }

  private void openFile(IEditorInput editorInput, String editorId) {
    IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
    try {
      page.openEditor(editorInput, editorId);
    } catch (PartInitException e) {
      e.printStackTrace();
    }
  }

  public String getTypeLabel() {
    return null;
  }

  public IRegion getHyperlinkRegion() {
    return region;
  }

  public String getHyperlinkText() {
    return null;
  }
}
