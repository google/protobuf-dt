/*
 * Copyright (c) 2011 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.ui.editor.spelling;

import static org.eclipse.xtext.nodemodel.util.NodeModelUtils.*;

import java.util.Iterator;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.jface.text.*;
import org.eclipse.jface.text.source.*;
import org.eclipse.ui.texteditor.spelling.*;
import org.eclipse.xtext.nodemodel.*;
import org.eclipse.xtext.resource.XtextResource;
import org.eclipse.xtext.ui.editor.model.XtextDocument;
import org.eclipse.xtext.util.concurrent.IUnitOfWork;

import com.google.eclipse.protobuf.model.util.INodes;
import com.google.eclipse.protobuf.protobuf.Import;

/**
 * @author alruiz@google.com (Alex Ruiz)
 */
class ProtobufSpelling extends SpellingReconcileStrategy {
  private final INodes nodes;

  ProtobufSpelling(ISourceViewer viewer, SpellingService spellingService, INodes nodes) {
    super(viewer, spellingService);
    this.nodes = nodes;
  }

  @Override public void setDocument(IDocument document) {
    super.setDocument(document);
  }

  @Override public void reconcile(IRegion region) {
    IAnnotationModel model = getAnnotationModel();
    if (model == null) {
      return;
    }
    super.reconcile(new Region(0, xtextDocument().getLength()));
    removeUnwantedAnnotations(model);
  }

  private void removeUnwantedAnnotations(final IAnnotationModel model) {
    xtextDocument().readOnly(new IUnitOfWork.Void<XtextResource>() {
      @Override public void process(XtextResource resource) throws Exception {
        removeUnwantedAnnotations(resource.getParseResult().getRootNode(), model);
      }
    });
  }

  private XtextDocument xtextDocument() {
    return (XtextDocument) super.getDocument();
  }

  @SuppressWarnings("unchecked")
  private void removeUnwantedAnnotations(INode rootNode, IAnnotationModel model) {
    Iterator<Annotation> iterator = model.getAnnotationIterator();
    while (iterator.hasNext()) {
      Annotation annotation = iterator.next();
      if (shouldRemoveFromModel(annotation, model, rootNode)) {
        model.removeAnnotation(annotation);
      }
    }
  }

  private boolean shouldRemoveFromModel(Annotation annotation, IAnnotationModel model, INode rootNode) {
    if (!(annotation instanceof SpellingAnnotation)) {
      return false;
    }
    SpellingAnnotation spellingAnnotation = (SpellingAnnotation) annotation;
    Position position = model.getPosition(spellingAnnotation);
    ILeafNode node = findLeafNodeAtOffset(rootNode, position.getOffset());
    return !shouldSpellCheck(node);
  }

  private boolean shouldSpellCheck(INode node) {
    if (node == null) {
      return false;
    }
    if (nodes.belongsToComment(node)) {
      return true;
    }
    if (nodes.belongsToString(node)) {
      EObject o = findActualSemanticObjectFor(node);
      return !(o instanceof Import);
    }
    return false;
  }
}
