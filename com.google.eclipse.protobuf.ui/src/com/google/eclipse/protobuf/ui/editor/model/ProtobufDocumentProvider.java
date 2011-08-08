/*
 * Copyright (c) 2011 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License v1.0 which
 * accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.ui.editor.model;

import static com.google.eclipse.protobuf.ui.exception.CoreExceptions.error;
import static com.google.eclipse.protobuf.util.Encodings.UTF_8;
import static org.eclipse.text.undo.DocumentUndoManagerRegistry.getDocumentUndoManager;

import org.eclipse.core.runtime.*;
import org.eclipse.jface.text.*;
import org.eclipse.jface.text.source.IAnnotationModel;
import org.eclipse.text.edits.TextEdit;
import org.eclipse.text.undo.IDocumentUndoManager;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.xtext.ui.editor.model.XtextDocument;
import org.eclipse.xtext.ui.editor.model.XtextDocumentProvider;

import com.google.inject.Inject;

/**
 * @author alruiz@google.com (Alex Ruiz)
 */
public class ProtobufDocumentProvider extends XtextDocumentProvider {

  @Inject private DocumentContentsFactoryRegistry contentsFactories;
  @Inject private SaveActions saveActions;

  @Override protected ElementInfo createElementInfo(Object element) throws CoreException {
    if (contentsFactories.findFactory(element) != null) return createElementInfo((IEditorInput) element);
    return super.createElementInfo(element);
  }

  private ElementInfo createElementInfo(IEditorInput input) throws CoreException {
    IDocument document = null;
    IStatus status = null;
    try {
      document = createDocument(input);
    } catch (CoreException e) {
      handleCoreException(e, "ProtobufDocumentProvider.createElementInfo");
      document = createEmptyDocument();
      status = e.getStatus();
    }
    IAnnotationModel model = createAnnotationModel(input);
    // new FileSynchronizer(input).install();
    FileInfo info = new FileInfo(document, model, null);
    info.fStatus = status;
    info.fEncoding = UTF_8;
    cacheEncodingState(input);
    return info;
  }

  @Override protected IDocument createDocument(Object element) throws CoreException {
    DocumentContentsFactory contentsFactory = contentsFactories.findFactory(element);
    if (contentsFactory != null) return createDocument(contentsFactory, element);
    return super.createDocument(element);
  }

  private IDocument createDocument(DocumentContentsFactory contentsFactory, Object element) throws CoreException {
    XtextDocument document = createEmptyDocument();
    contentsFactory.createContents(document, element);
    return document;
  }

  @Override protected void doSaveDocument(IProgressMonitor monitor, Object element, IDocument document,
      boolean overwrite) throws CoreException {
    if (element instanceof IFileEditorInput) performSaveActions(document);
    super.doSaveDocument(monitor, element, document, overwrite);
  }

  private void performSaveActions(IDocument document) throws CoreException {
    TextEdit edit = saveActions.createSaveAction(document, new IRegion[] { allOf(document) });
    if (edit == null) return;
    try {
      IDocumentUndoManager manager = getDocumentUndoManager(document);
      manager.beginCompoundChange();
      edit.apply(document);
      manager.endCompoundChange();
    } catch (Throwable t) {
      throw error(t);
    }
  }

  private Region allOf(IDocument document) {
    return new Region(0, document.getLength());
  }
}
