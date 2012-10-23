/*
 * Copyright (c) 2011 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.ui.editor.model;

import static com.google.common.io.Closeables.closeQuietly;
import static com.google.eclipse.protobuf.ui.util.IStatusFactory.error;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.emf.common.util.URI;
import org.eclipse.ui.IEditorInput;
import org.eclipse.xtext.resource.XtextResource;
import org.eclipse.xtext.ui.editor.model.XtextDocument;

import com.google.eclipse.protobuf.ui.editor.UriEditorInput;
import com.google.eclipse.protobuf.ui.resource.XtextResourceFactory;
import com.google.inject.Inject;

/**
 * @author alruiz@google.com (Alex Ruiz)
 */
class UriDocumentContentsFactory implements DocumentContentsFactory {
  @Inject private ContentReader contentReader;
  @Inject private XtextResourceFactory resourceFactory;

  @Override public void createContents(XtextDocument document, Object element) throws CoreException {
    UriEditorInput input = supportedEditorInputType().cast(element);
    URI uri = input.getFileUri();
    try {
      String contents = contentsOf(uri);
      document.set(contents);
      XtextResource resource = resourceFactory.createResource(uri, contents);
      document.setInput(resource);
    } catch (Throwable t) {
      throw new CoreException(error(t));
    }
  }

  private String contentsOf(URI uri) throws IOException {
    InputStream inputStream = null;
    try {
      URL url = new URL(uri.toString());
      inputStream = url.openConnection().getInputStream();
      return contentReader.contentsOf(inputStream);
    } finally {
      closeQuietly(inputStream);
    }
  }

  @Override public boolean supportsEditorInputType(IEditorInput input) {
    return supportedEditorInputType().isInstance(input);
  }

  private Class<UriEditorInput> supportedEditorInputType() {
    return UriEditorInput.class;
  }
}