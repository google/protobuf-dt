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
import static com.google.eclipse.protobuf.ui.util.Resources.URI_SCHEME_FOR_FILES;
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
import java.net.URI;

import org.eclipse.core.filesystem.*;
import org.eclipse.core.runtime.*;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.source.IAnnotationModel;
import org.eclipse.ui.IURIEditorInput;
import org.eclipse.ui.ide.FileStoreEditorInput;
import org.eclipse.xtext.resource.XtextResource;
import org.eclipse.xtext.ui.editor.model.*;
import org.eclipse.xtext.ui.resource.IResourceSetProvider;
import org.eclipse.xtext.util.StringInputStream;

import com.google.eclipse.protobuf.ui.util.*;
import com.google.inject.Inject;

/**
 * @author alruiz@google.com (Alex Ruiz)
 */
public class ProtobufDocumentProvider extends XtextDocumentProvider {

  @Inject private IResourceSetProvider resourceSetProvider;
  @Inject private Resources resources;

  @Override protected ElementInfo createElementInfo(Object element) throws CoreException {
    if (element instanceof FileStoreEditorInput) return createElementInfo((FileStoreEditorInput) element);
    return super.createElementInfo(element);
  }

  private ElementInfo createElementInfo(FileStoreEditorInput input) throws CoreException {
    IDocument document = null;
    IStatus status = null;
    try {
      document = createDocument(input);
    } catch (CoreException e) {
      handleCoreException(e, "ProtobufDocumentProvider.createElementInfo");
      document = createEmptyDocument();
      status = e.getStatus();
    }
    IFileStore store = EFS.getLocalFileSystem().getStore(fileFrom(input).toURI());
    IFileInfo fileInfo = store.fetchInfo();
    IAnnotationModel model = createAnnotationModel(input);
    // new FileSynchronizer(input).install();
    FileInfo info = new FileInfo(document, model, null);
    info.fModificationStamp = fileInfo.getLastModified();
    info.fStatus = status;
    info.fEncoding = UTF_8;
    cacheEncodingState(input);
    return info;
  }

  @Override protected IDocument createDocument(Object element) throws CoreException {
    if (element instanceof FileStoreEditorInput) return createDocument((FileStoreEditorInput) element);
    return super.createDocument(element);
  }

  private IDocument createDocument(FileStoreEditorInput input) throws CoreException {
    XtextDocument document = createEmptyDocument();
    File file = fileFrom(input);
    try {
      String contents = contentsOf(file);
      document.set(contents);
      XtextResource resource = createResource(file.toURI().toString(), new StringInputStream(contents));
      document.setInput(resource);
      return document;
    } catch (Throwable t) {
      String message = t.getMessage();
      if (message == null) message = "";
      throw new CoreException(new Status(ERROR, PLUGIN_ID, message, t));
    }
  }

  private File fileFrom(IURIEditorInput input) {
    URI uri = input.getURI();
    String scheme = uri.getScheme();
    if (scheme != URI_SCHEME_FOR_FILES) {
      String cleanUri = uri.toString().replaceFirst(scheme, URI_SCHEME_FOR_FILES);
      uri = URI.create(cleanUri);
    }
    return new File(uri);
  }

  private String contentsOf(File file) throws IOException {
    Reader reader = null;
    InputStream inputStream = null;
    try {
      inputStream = new FileInputStream(file);
      reader = new BufferedReader(readerFor(inputStream), DEFAULT_FILE_SIZE);
      StringBuilder contents = new StringBuilder(DEFAULT_FILE_SIZE);
      char[] buffer = new char[2048];
      int character = reader.read(buffer);
      while (character > 0) {
        contents.append(buffer, 0, character);
        character = reader.read(buffer);
      }
      return contents.toString();
    } finally {
      if (!close(reader)) close(inputStream);
    }
  }

  private Reader readerFor(InputStream inputStream) throws IOException {
    return new InputStreamReader(inputStream, UTF_8);
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
}
