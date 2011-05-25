/*
 * Copyright (c) 2011 Google Inc. All rights reserved. This program and the accompanying materials are made available
 * under the terms of the Eclipse Public License v1.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.ui.editor.model;

import static com.google.eclipse.protobuf.ui.ProtobufUiModule.PLUGIN_ID;
import static org.eclipse.core.runtime.IStatus.ERROR;
import static org.eclipse.emf.common.util.URI.createURI;
import static org.eclipse.xtext.validation.CheckMode.FAST_ONLY;

import java.io.*;
import java.net.URI;

import org.eclipse.core.filesystem.*;
import org.eclipse.core.runtime.*;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.source.IAnnotationModel;
import org.eclipse.ui.IURIEditorInput;
import org.eclipse.ui.ide.FileStoreEditorInput;
import org.eclipse.xtext.parser.IParseResult;
import org.eclipse.xtext.parser.IParser;
import org.eclipse.xtext.resource.XtextResource;
import org.eclipse.xtext.ui.editor.model.XtextDocument;
import org.eclipse.xtext.ui.editor.model.XtextDocumentProvider;
import org.eclipse.xtext.ui.editor.quickfix.IssueResolutionProvider;
import org.eclipse.xtext.ui.editor.validation.AnnotationIssueProcessor;
import org.eclipse.xtext.ui.editor.validation.ValidationJob;
import org.eclipse.xtext.util.StringInputStream;
import org.eclipse.xtext.validation.IResourceValidator;

import com.google.eclipse.protobuf.ui.util.Closeables;
import com.google.inject.Inject;

/**
 * @author alruiz@google.com (Alex Ruiz)
 */
public class ProtobufDocumentProvider extends XtextDocumentProvider {

  private static final String ENCODING = "UTF-8";
  
  @Inject private Closeables closeables;
  @Inject private IssueResolutionProvider issueResolutionProvider;
  @Inject private IParser parser;
  @Inject private IResourceValidator resourceValidator;
  
  @Override protected ElementInfo createElementInfo(Object element) throws CoreException {
    if (element instanceof FileStoreEditorInput) {
      FileStoreEditorInput input = (FileStoreEditorInput) element;
      IDocument document = null;
      IStatus status = null;
      try {
        document = createDocument(element);
      } catch (CoreException e) {
        handleCoreException(e, "ProtobufDocumentProvider.createElementInfo");
        document = createEmptyDocument();
        status = e.getStatus();
      }
      IFileStore store = EFS.getLocalFileSystem().getStore(fileFrom(input).toURI());
      IFileInfo fileInfo = store.fetchInfo();
      IAnnotationModel model = createAnnotationModel(element);
      // new FileSynchronizer(input).install();
      FileInfo info = new FileInfo(document, model, null);
      info.fModificationStamp = fileInfo.getLastModified();
      info.fStatus = status;
      info.fEncoding = ENCODING;
      cacheEncodingState(element);
      XtextDocument xtextDocument = (XtextDocument) document;
      AnnotationIssueProcessor annotationIssueProcessor = new AnnotationIssueProcessor(xtextDocument, model,
          issueResolutionProvider);
      ValidationJob job = new ValidationJob(resourceValidator, xtextDocument, annotationIssueProcessor, FAST_ONLY);
      xtextDocument.setValidationJob(job);
      return info;
    }
    return super.createElementInfo(element);
  }

  /** {@inheritDoc} */
  @Override protected IDocument createDocument(Object element) throws CoreException {
    if (element instanceof FileStoreEditorInput) {
      XtextDocument document = createEmptyDocument();
      FileStoreEditorInput input = (FileStoreEditorInput) element;
      File file = fileFrom(input);
      XtextResource resource = new XtextResource(createURI(file.toURI().toString()));
      try {
        String contents = contentsOf(file);
        document.set(contents);
        IParseResult result = parser.parse(new InputStreamReader(new StringInputStream(contents), ENCODING));
        resource.getContents().add(result.getRootASTElement());
        document.setInput(resource);
        return document;
      } catch (Throwable t) {
        if (t instanceof CoreException) throw (CoreException) t;
        throw wrapWithCoreException(t);
      }
    }
    return super.createDocument(element);
  }
  
  private File fileFrom(IURIEditorInput input) {
    URI uri = input.getURI();
    String scheme = uri.getScheme();
    if (scheme != "file") {
      String cleanUri = uri.toString().replaceFirst(scheme, "file");
      uri = URI.create(cleanUri);
    }
    return new File(uri);
  }

  private String contentsOf(File file) throws CoreException {
    Reader reader = null;
    InputStream contentStream = null;
    try {
      contentStream = new FileInputStream(file);
      reader = new BufferedReader(new InputStreamReader(contentStream, ENCODING), DEFAULT_FILE_SIZE);
      StringBuilder contents = new StringBuilder(DEFAULT_FILE_SIZE);
      char[] buffer = new char[2048];
      int character = reader.read(buffer);
      while (character > 0) {
        contents.append(buffer, 0, character);
        character = reader.read(buffer);
      }
      return contents.toString();
    } catch (IOException e) {
      throw wrapWithCoreException(e);
    } finally {
      if (!closeables.close(reader)) closeables.close(contentStream);
    }
  }
  
  private CoreException wrapWithCoreException(Throwable cause) {
    return new CoreException(new Status(ERROR, PLUGIN_ID, messageOf(cause), cause));
  }
  
  private String messageOf(Throwable t) {
    String message = t.getMessage();
    return (message != null) ? message : "";
  }
}
