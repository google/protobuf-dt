/*
 * Copyright (c) 2012 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.cdt.util;

import org.eclipse.core.resources.IFile;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.texteditor.ITextEditor;

import com.google.inject.Singleton;

/**
 * Utility methods related to editors.
 *
 * @author alruiz@google.com (Alex Ruiz)
 */
@Singleton public class Editors {

  /**
   * Returns the offset of the selected text in the given editor.
   * @param editor the given editor.
   * @return  the offset of the selected text in the given editor, or -1 if there is no valid text information.
   */
  public int selectionOffsetOf(IEditorPart editor) {
    ISelectionProvider selectionProvider = ((ITextEditor) editor).getSelectionProvider();
    ISelection selection = selectionProvider.getSelection();
    if (selection instanceof ITextSelection) {
      ITextSelection textSelection = (ITextSelection) selection;
      return textSelection.getOffset();
    }
    return -1;
  }

  /**
   * Returns the file displayed in the given editor.
   * @param editor the given editor.
   * @return the file displayed in the given editor.
   */
  public IFile fileDisplayedIn(IEditorPart editor) {
    return (IFile) editor.getEditorInput().getAdapter(IFile.class);
  }
}
