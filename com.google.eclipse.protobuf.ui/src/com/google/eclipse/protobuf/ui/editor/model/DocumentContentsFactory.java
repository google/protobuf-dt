/*
 * Copyright (c) 2011 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.ui.editor.model;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.ui.IEditorInput;
import org.eclipse.xtext.ui.editor.model.XtextDocument;

/**
 * @author alruiz@google.com (Alex Ruiz)
 */
interface DocumentContentsFactory {
  void createContents(XtextDocument document, Object element) throws CoreException;

  boolean supportsEditorInputType(IEditorInput input);
}
