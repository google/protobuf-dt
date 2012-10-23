/*
 * Copyright (c) 2011 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.ui.commands;

import static org.eclipse.xtext.ui.editor.utils.EditorUtils.getActiveXtextEditor;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.widgets.Control;
import org.eclipse.xtext.ui.editor.XtextEditor;

/**
 * Base class for command handlers that insert content in an editor.
 *
 * @author alruiz@google.com (Alex Ruiz)
 */
public abstract class SmartInsertHandler extends AbstractHandler {
  /** {@inheritDoc} */
  @Override public final Object execute(ExecutionEvent event) {
    XtextEditor activeEditor = getActiveXtextEditor();
    if (activeEditor != null) {
      insertContent(activeEditor, styledTextFrom(activeEditor));
    }
    return null;
  }

  private StyledText styledTextFrom(XtextEditor editor) {
    Object adapter = editor.getAdapter(Control.class);
    return (adapter instanceof StyledText) ? (StyledText) adapter : null;
  }

  protected abstract void insertContent(XtextEditor editor, StyledText styledText);
}
