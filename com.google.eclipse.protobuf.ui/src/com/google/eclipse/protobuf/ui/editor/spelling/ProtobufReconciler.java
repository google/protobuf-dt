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

import static org.eclipse.ui.editors.text.EditorsUI.getSpellingService;

import com.google.inject.Inject;

import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.xtext.ui.editor.reconciler.XtextReconciler;

/**
 * @author alruiz@google.com (Alex Ruiz)
 */
public class ProtobufReconciler extends XtextReconciler {

  private boolean installed;

  @Inject public ProtobufReconciler(ProtobufReconcileStrategy strategy) {
    super(strategy);
  }

  @Override public void install(ITextViewer textViewer) {
    if (installed) return;
    super.install(textViewer);
    if (textViewer instanceof ISourceViewer) {
      ISourceViewer viewer = (ISourceViewer) textViewer;
      ProtobufReconcileStrategy strategy = (ProtobufReconcileStrategy) getReconcilingStrategy("");
      strategy.addSpellSupport(viewer, getSpellingService());
    }
    installed = true;
  }
}
