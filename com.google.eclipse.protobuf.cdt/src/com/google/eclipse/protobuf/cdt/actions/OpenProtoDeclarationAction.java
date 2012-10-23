/*
 * Copyright (c) 2012 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.cdt.actions;

import org.eclipse.emf.common.util.URI;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IEditorActionDelegate;
import org.eclipse.ui.IEditorPart;
import org.eclipse.xtext.ui.editor.IURIEditorOpener;

import com.google.inject.Inject;

/**
 * Opens the declaration of a C++ element in the corresponding .proto file.
 *
 * @author alruiz@google.com (Alex Ruiz)
 */
public class OpenProtoDeclarationAction implements IEditorActionDelegate {
  @Inject private IURIEditorOpener editorOpener;
  @Inject private ProtobufElementUriFinder uriFinder;

  private IEditorPart editor;

  @Override public void run(IAction action) {
    if (editor == null) {
      return;
    }
    URI foundUri = uriFinder.findProtobufElementLocationFromSelectionOf(editor);
    if (foundUri != null) {
      editorOpener.open(foundUri, true);
    }
  }

  @Override public void setActiveEditor(IAction action, IEditorPart editor) {
    this.editor = editor;
  }

  @Override public void selectionChanged(IAction action, ISelection selection) {}
}
