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
import static org.eclipse.core.filebuffers.FileBuffers.getTextFileBufferManager;
import static org.eclipse.core.filebuffers.LocationKind.*;
import static org.eclipse.text.undo.DocumentUndoManagerRegistry.getDocumentUndoManager;

import org.eclipse.core.filebuffers.*;
import org.eclipse.core.runtime.*;
import org.eclipse.jface.text.*;
import org.eclipse.jface.text.source.IAnnotationModel;
import org.eclipse.text.edits.TextEdit;
import org.eclipse.text.undo.IDocumentUndoManager;
import org.eclipse.ui.*;
import org.eclipse.xtext.ui.editor.model.*;
import org.eclipse.xtext.ui.editor.preferences.IPreferenceStoreAccess;

import com.google.eclipse.protobuf.ui.preferences.editor.save.core.SaveActionsPreferences;
import com.google.eclipse.protobuf.ui.util.editor.Editors;
import com.google.inject.Inject;

/**
 * @author alruiz@google.com (Alex Ruiz)
 */
public class ProtobufDocumentProvider extends XtextDocumentProvider {

  private static final IRegion[] NO_CHANGE = new IRegion[0];

  @Inject private Editors editors;
  @Inject private DocumentContentsFactoryRegistry documentContentsFactories;
  @Inject private IPreferenceStoreAccess storeAccess;
  @Inject private SaveActions saveActions;

  @Override protected ElementInfo createElementInfo(Object element) throws CoreException {
    if (documentContentsFactories.findFactory(element) != null) {
      return createElementInfo((IEditorInput) element);
    }
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
    DocumentContentsFactory factory = documentContentsFactories.findFactory(element);
    if (factory != null) {
      return createDocument(factory, element);
    }
    return super.createDocument(element);
  }

  private IDocument createDocument(DocumentContentsFactory contentsFactory, Object element) throws CoreException {
    XtextDocument document = createEmptyDocument();
    contentsFactory.createContents(document, element);
    return document;
  }

  @Override protected void doSaveDocument(IProgressMonitor monitor, Object element, IDocument document,
      boolean overwrite) throws CoreException {
    if (element instanceof IFileEditorInput) {
      performSaveActions(monitor, (IFileEditorInput) element, document);
    }
    super.doSaveDocument(monitor, element, document, overwrite);
  }

  private void performSaveActions(IProgressMonitor monitor,
      IFileEditorInput editorInput, IDocument document) throws CoreException {
    IRegion[] changedRegions = changedRegions(monitor, editorInput, document);
    TextEdit edit = saveActions.createSaveAction(document, changedRegions);
    if (edit == null) {
      return;
    }
    try {
      IDocumentUndoManager manager = getDocumentUndoManager(document);
      manager.beginCompoundChange();
      edit.apply(document);
      manager.endCompoundChange();
    } catch (Throwable t) {
      throw error(t);
    }
  }

  private IRegion[] changedRegions(IProgressMonitor monitor, IFileEditorInput editorInput, IDocument document)
      throws CoreException {
    SaveActionsPreferences preferences = new SaveActionsPreferences(storeAccess);
    if (!preferences.removeTrailingWhitespace().getValue()) {
      return NO_CHANGE;
    }
    if (preferences.inEditedLines().getValue()) {
      return editors.calculateChangedLineRegions(textFileBuffer(monitor, editorInput), document, monitor);
    }
    return new IRegion[] { new Region(0, document.getLength()) };
  }

  private ITextFileBuffer textFileBuffer(IProgressMonitor monitor, IFileEditorInput editorInput) throws CoreException {
    IPath location = editorInput.getFile().getFullPath();
    ITextFileBufferManager textFileBufferManager = getTextFileBufferManager();
    textFileBufferManager.connect(location, NORMALIZE, monitor);
    try {
      return textFileBufferManager.getTextFileBuffer(location, IFILE);
    } finally {
      textFileBufferManager.disconnect(location, NORMALIZE, monitor);
    }
  }
}
