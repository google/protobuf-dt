/*
 * Copyright (c) 2011 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.ui.editor.model;

import static com.google.eclipse.protobuf.ui.exception.CoreExceptions.error;
import static com.google.eclipse.protobuf.util.Closeables.closeQuietly;

import java.io.*;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.ide.FileStoreEditorInput;
import org.eclipse.xtext.resource.XtextResource;
import org.eclipse.xtext.ui.editor.model.XtextDocument;

import com.google.eclipse.protobuf.ui.resource.XtextResourceFactory;
import com.google.inject.Inject;

/**
 * @author alruiz@google.com (Alex Ruiz)
 */
class FileStoreDocumentContentsFactory implements DocumentContentsFactory {

  @Inject private ContentReader contentReader;
  @Inject private UriEditorInputs files;
  @Inject private XtextResourceFactory resourceFactory;

  @Override public void createContents(XtextDocument document, Object element) throws CoreException {
    FileStoreEditorInput input = supportedEditorInputType().cast(element);
    File file = files.fileFrom(input);
    try {
      String contents = contentsOf(file);
      document.set(contents);
      XtextResource resource = resourceFactory.createResource(file.toURI().toString(), contents);
      document.setInput(resource);
    } catch (Throwable t) {
      throw error(t);
    }
  }

  private String contentsOf(File file) throws IOException {
    InputStream inputStream = null;
    try {
      inputStream = new FileInputStream(file);
      return contentReader.contentsOf(inputStream);
    } finally {
      closeQuietly(inputStream);
    }
  }

  @Override public boolean supportsEditorInputType(IEditorInput input) {
    return supportedEditorInputType().isInstance(input);
  }

  private Class<FileStoreEditorInput> supportedEditorInputType() {
    return FileStoreEditorInput.class;
  }
}