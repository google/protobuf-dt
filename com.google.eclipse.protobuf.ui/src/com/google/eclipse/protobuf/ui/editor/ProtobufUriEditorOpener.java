/*
 * Copyright (c) 2011 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.ui.editor;

import static org.eclipse.xtext.ui.editor.utils.EditorUtils.getXtextEditor;

import org.apache.log4j.Logger;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.PartInitException;
import org.eclipse.xtext.ui.editor.LanguageSpecificURIEditorOpener;

import com.google.inject.Inject;

/**
 * @author alruiz@google.com (Alex Ruiz)
 */
public class ProtobufUriEditorOpener extends LanguageSpecificURIEditorOpener {
  private static Logger logger = Logger.getLogger(ProtobufUriEditorOpener.class);

  @Inject private FileOpener fileOpener;

  /** {@inheritDoc} */
  @Override public IEditorPart open(URI uri, EReference crossReference, int indexInList, boolean select) {
    try {
      IEditorPart editor = editorFor(uri.trimFragment());
      if (editor != null) {
        selectAndReveal(editor, uri, crossReference, indexInList, select);
        return getXtextEditor(editor);
      }
    } catch (PartInitException e) {
      logger.error("Unable to open " + uri.toString(), e);
    }
    return super.open(uri, crossReference, indexInList, select);
  }

  private IEditorPart editorFor(URI uri) throws PartInitException {
    if (uri.isFile()) {
      return fileOpener.openProtoFileInFileSystem(uri);
    }
    if (uri.isPlatformPlugin()) {
      return fileOpener.openProtoFileInPlugin(uri);
    }
    return null;
  }
}
