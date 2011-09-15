/*
 * Copyright (c) 2011 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.ui.editor.spelling;

import static org.eclipse.xtext.nodemodel.util.NodeModelUtils.findLeafNodeAtOffset;
import static org.eclipse.xtext.util.Strings.isEmpty;

import java.util.Iterator;

import org.eclipse.jface.text.*;
import org.eclipse.jface.text.source.*;
import org.eclipse.ui.texteditor.spelling.*;
import org.eclipse.xtext.nodemodel.*;
import org.eclipse.xtext.parser.IParseResult;
import org.eclipse.xtext.resource.XtextResource;
import org.eclipse.xtext.ui.editor.model.XtextDocument;
import org.eclipse.xtext.util.concurrent.IUnitOfWork;

import com.google.eclipse.protobuf.util.ModelNodes;

/**
 * @author alruiz@google.com (Alex Ruiz)
 */
class ProtobufSpelling extends SpellingReconcileStrategy {

  private final ModelNodes nodes;

  ProtobufSpelling(ISourceViewer viewer, SpellingService spellingService, ModelNodes nodes) {
    super(viewer, spellingService);
    this.nodes = nodes;
  }

  @Override public void setDocument(IDocument document) {
    super.setDocument(document);
  }

  public void reconcile(IRegion region) {
    super.reconcile(new Region(0, xtextDocument().getLength()));
    removeUnwantedAnnotations();
  }

  private void removeUnwantedAnnotations() {
    xtextDocument().readOnly(new IUnitOfWork.Void<XtextResource>() {
      @Override public void process(XtextResource resource) throws Exception {
        removeUnwantedAnnotations(resource.getParseResult());
      }
    });
  }

  private XtextDocument xtextDocument() {
    return (XtextDocument) super.getDocument();
  }

  private void removeUnwantedAnnotations(IParseResult parseResult) {
    IAnnotationModel model = getAnnotationModel();
    ICompositeNode rootNode = parseResult.getRootNode();
    for (Annotation annotation : annotations()) {
      if (shouldRemoveFromModel(annotation, model, rootNode)) model.removeAnnotation(annotation);
    }
  }

  private Iterable<Annotation> annotations() {
    return new Iterable<Annotation>() {
      @SuppressWarnings("unchecked") public Iterator<Annotation> iterator() {
        return getAnnotationModel().getAnnotationIterator();
      }
    };
  }

  private boolean shouldRemoveFromModel(Annotation annotation, IAnnotationModel model, INode rootNode) {
    if (!(annotation instanceof SpellingAnnotation)) return false;
    SpellingAnnotation spellingAnnotation = (SpellingAnnotation) annotation;
    Position position = model.getPosition(spellingAnnotation);
    ILeafNode node = findLeafNodeAtOffset(rootNode, position.getOffset());
    return !shouldSpellCheck(node);
  }

  private boolean shouldSpellCheck(INode node) {
    if (node == null) return false;
    String text = node.getText();
    if (isEmpty(text) || isEmpty(text.trim())) return false;
    return nodes.isCommentOrString(node);
  }
}
