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

import static org.eclipse.core.filebuffers.FileBuffers.getTextFileBufferManager;
import static org.eclipse.core.filebuffers.LocationKind.IFILE;
import static org.eclipse.core.filebuffers.LocationKind.NORMALIZE;
import static org.eclipse.text.undo.DocumentUndoManagerRegistry.getDocumentUndoManager;

import static com.google.common.collect.Lists.newArrayList;
import static com.google.eclipse.protobuf.ui.util.IStatusFactory.error;

import java.util.List;

import org.eclipse.core.filebuffers.ITextFileBuffer;
import org.eclipse.core.filebuffers.ITextFileBufferManager;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.Region;
import org.eclipse.text.edits.TextEdit;
import org.eclipse.text.undo.IDocumentUndoManager;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.xtext.ui.editor.model.XtextDocument;
import org.eclipse.xtext.ui.editor.model.XtextDocumentProvider;

import com.google.eclipse.protobuf.ui.preferences.editor.save.SaveActionsPreferences;
import com.google.eclipse.protobuf.ui.util.editor.ChangedLineRegionCalculator;
import com.google.inject.Inject;
import com.google.inject.Provider;

/**
 * @author alruiz@google.com (Alex Ruiz)
 */
public class ProtobufDocumentProvider extends XtextDocumentProvider {
  private static final IRegion[] NO_CHANGE = new IRegion[0];

  @Inject private ChangedLineRegionCalculator calculator;
  @Inject private Provider<SaveActionsPreferences> preferencesProvider;
  @Inject private SaveActions saveActions;

  private final List<DocumentContentsFactory> documentFactories;

  @Inject public ProtobufDocumentProvider(FileStoreDocumentContentsFactory f1, UriDocumentContentsFactory f2) {
    documentFactories = newArrayList(f1, f2);
  }

  @Override protected IDocument createDocument(Object element) throws CoreException {
    IDocument document = super.createDocument(element);
    if (document != null) {
      return document;
    }
    DocumentContentsFactory factory = findDocumentFactory(element);
    if (factory != null) {
      return createDocument(factory, element);
    }
    return null;
  }

  private DocumentContentsFactory findDocumentFactory(Object element) {
    if (element instanceof IEditorInput) {
      IEditorInput input = (IEditorInput) element;
      for (DocumentContentsFactory factory : documentFactories) {
        if (factory.supportsEditorInputType(input)) {
          return factory;
        }
      }
    }
    return null;
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
      throw new CoreException(error(t));
    }
  }

  private IRegion[] changedRegions(IProgressMonitor monitor, IFileEditorInput editorInput, IDocument document)
      throws CoreException {
    SaveActionsPreferences preferences = preferencesProvider.get();
    if (!preferences.shouldRemoveTrailingWhitespace()) {
      return NO_CHANGE;
    }
    if (preferences.shouldRemoveTrailingWhitespaceInEditedLines()) {
      return calculator.calculateChangedLineRegions(textFileBuffer(monitor, editorInput), document, monitor);
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
