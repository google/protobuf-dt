/*
 * Copyright (c) 2011 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.ui.editor.model;

import static com.google.eclipse.protobuf.ui.ProtobufUiModule.PLUGIN_ID;
import static com.google.eclipse.protobuf.util.Closeables.close;
import static org.eclipse.core.runtime.IStatus.ERROR;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Status;
import org.eclipse.emf.common.util.URI;
import org.eclipse.ui.IEditorInput;
import org.eclipse.xtext.ui.editor.model.XtextDocument;

import com.google.eclipse.protobuf.scoping.ProtoDescriptor;
import com.google.eclipse.protobuf.scoping.ProtoDescriptorProvider;
import com.google.eclipse.protobuf.ui.editor.UriEditorInput;
import com.google.inject.Inject;

/**
 * @author alruiz@google.com (Alex Ruiz)
 */
class UriDocumentContentsFactory implements DocumentContentsFactory {

  @Inject private ProtoDescriptorProvider descriptorProvider;
  @Inject private ContentReader contentReader;

  public void createContents(XtextDocument document, Object element) throws CoreException {
    UriEditorInput input = supportedEditorInputType().cast(element);
    URI uri = input.getFileUri().trimFragment();
    if (!descriptorProvider.descriptorLocation().equals(uri)) {
      throw new UnsupportedOperationException("File to open is not descriptor.proto");
    }
    ProtoDescriptor descriptor = descriptorProvider.get();
    try {
      String contents = contentsOf(uri);
      document.set(contents);
      document.setInput(descriptor.resource());
    } catch (Throwable t) {
      String message = t.getMessage();
      if (message == null) message = "";
      // TODO remove duplication
      throw new CoreException(new Status(ERROR, PLUGIN_ID, message, t));
    }
  }

  private String contentsOf(URI uri) throws IOException {
    InputStream inputStream = null;
    try {
      URL url = new URL(uri.toString());
      inputStream = url.openConnection().getInputStream();
      return contentReader.contentsOf(inputStream);
    } finally {
      close(inputStream);
    }
  }

  public boolean supportsEditorInputType(IEditorInput input) {
    return supportedEditorInputType().isInstance(input);
  }

  private Class<UriEditorInput> supportedEditorInputType() {
    return UriEditorInput.class;
  }
}