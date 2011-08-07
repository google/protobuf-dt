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
import static com.google.eclipse.protobuf.util.Encodings.UTF_8;
import static java.util.Collections.singletonMap;
import static org.eclipse.core.runtime.IStatus.ERROR;
import static org.eclipse.emf.common.util.URI.createURI;
import static org.eclipse.emf.ecore.resource.ContentHandler.UNSPECIFIED_CONTENT_TYPE;
import static org.eclipse.xtext.EcoreUtil2.resolveLazyCrossReferences;
import static org.eclipse.xtext.resource.XtextResource.OPTION_ENCODING;
import static org.eclipse.xtext.util.CancelIndicator.NullImpl;

import java.io.*;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Status;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.ide.FileStoreEditorInput;
import org.eclipse.xtext.resource.XtextResource;
import org.eclipse.xtext.ui.editor.model.XtextDocument;
import org.eclipse.xtext.ui.resource.IResourceSetProvider;
import org.eclipse.xtext.util.StringInputStream;

import com.google.eclipse.protobuf.ui.util.Resources;
import com.google.inject.Inject;

/**
 * @author alruiz@google.com (Alex Ruiz)
 */
class FileStoreDocumentContentsFactory implements DocumentContentsFactory {

  @Inject private IResourceSetProvider resourceSetProvider;
  @Inject private Resources resources;
  @Inject private Files files;
  @Inject private ContentReader contentReader;

  public void createContents(XtextDocument document, Object element) throws CoreException {
    FileStoreEditorInput input = supportedEditorInputType().cast(element);
    File file = files.fileFrom(input);
    try {
      String contents = contentsOf(file);
      document.set(contents);
      XtextResource resource = createResource(file.toURI().toString(), new StringInputStream(contents));
      document.setInput(resource);
    } catch (Throwable t) {
      String message = t.getMessage();
      if (message == null) message = "";
      throw new CoreException(new Status(ERROR, PLUGIN_ID, message, t));
    }
  }

  private String contentsOf(File file) throws IOException {
    InputStream inputStream = null;
    try {
      inputStream = new FileInputStream(file);
      return contentReader.contentsOf(inputStream);
    } finally {
      close(inputStream);
    }
  }

  private XtextResource createResource(String uri, InputStream input) {
    ResourceSet resourceSet = resourceSetProvider.get(resources.activeProject());
    XtextResource resource = (XtextResource) resourceSet.createResource(createURI(uri), UNSPECIFIED_CONTENT_TYPE);
    try {
      resource.load(input, singletonMap(OPTION_ENCODING, UTF_8));
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
    resolveLazyCrossReferences(resource, NullImpl);
    return resource;
  }

  public boolean supportsEditorInputType(IEditorInput input) {
    return supportedEditorInputType().isInstance(input);
  }

  private Class<FileStoreEditorInput> supportedEditorInputType() {
    return FileStoreEditorInput.class;
  }
}