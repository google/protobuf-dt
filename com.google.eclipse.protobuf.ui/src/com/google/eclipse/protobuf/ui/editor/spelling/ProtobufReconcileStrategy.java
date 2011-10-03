/*
 * Copyright (c) 2011 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License v1.0 which
 * accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.ui.editor.spelling;

import org.eclipse.jface.text.*;
import org.eclipse.jface.text.reconciler.DirtyRegion;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.ui.texteditor.spelling.SpellingService;
import org.eclipse.xtext.ui.editor.reconciler.XtextDocumentReconcileStrategy;

import com.google.eclipse.protobuf.model.util.INodes;
import com.google.inject.Inject;

/**
 * @author alruiz@google.com (Alex Ruiz)
 */
public class ProtobufReconcileStrategy extends XtextDocumentReconcileStrategy {

  private ProtobufSpelling spellingStrategy;

  @Inject private INodes nodes;

  @Override public void reconcile(DirtyRegion dirtyRegion, IRegion subRegion) {
    super.reconcile(dirtyRegion, subRegion);
    if (spellingStrategy == null) return;
    spellingStrategy.reconcile(dirtyRegion, subRegion);
  }

  @Override public void reconcile(IRegion subRegion) {
    super.reconcile(subRegion);
    initialReconcile();
  }

  @Override public void setDocument(IDocument document) {
    super.setDocument(document);
    if (spellingStrategy == null) return;
    spellingStrategy.setDocument(document);
    initialReconcile();
  }

  private void initialReconcile() {
    if (spellingStrategy == null) return;
    spellingStrategy.initialReconcile();
  }

  void addSpellSupport(ISourceViewer viewer, SpellingService spellingService) {
    if (spellingStrategy != null) return;
    spellingStrategy = new ProtobufSpelling(viewer, spellingService, nodes);
  }
}
