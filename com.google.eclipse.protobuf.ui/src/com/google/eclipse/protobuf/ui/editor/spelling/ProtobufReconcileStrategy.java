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

import static java.util.Collections.emptyList;
import static org.eclipse.xtext.nodemodel.util.NodeModelUtils.findLeafNodeAtOffset;
import static org.eclipse.xtext.util.Strings.isEmpty;

import com.google.eclipse.protobuf.protobuf.Import;
import com.google.eclipse.protobuf.util.ModelNodes;
import com.google.inject.Inject;

import org.eclipse.jface.text.*;
import org.eclipse.jface.text.reconciler.DirtyRegion;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.ui.texteditor.spelling.*;
import org.eclipse.xtext.nodemodel.*;
import org.eclipse.xtext.nodemodel.impl.AbstractNode;
import org.eclipse.xtext.parser.IParseResult;
import org.eclipse.xtext.resource.XtextResource;
import org.eclipse.xtext.ui.editor.model.XtextDocument;
import org.eclipse.xtext.ui.editor.reconciler.XtextDocumentReconcileStrategy;
import org.eclipse.xtext.util.concurrent.IUnitOfWork;

import java.util.*;

/**
 * @author alruiz@google.com (Alex Ruiz)
 */
public class ProtobufReconcileStrategy extends XtextDocumentReconcileStrategy {

  private SpellingReconcileStrategy spellingStrategy;
  private XtextDocument document;

  @Inject private ModelNodes nodes;

  @Override public void reconcile(DirtyRegion dirtyRegion, IRegion subRegion) {
    super.reconcile(dirtyRegion, subRegion);
    if (spellingStrategy == null) return;
    INode node = nodeToSpellCheck(subRegion);
    if (node != null) spellingStrategy.reconcile(dirtyRegion, new NodeBasedRegion(node));
  }

  private INode nodeToSpellCheck(final IRegion subRegion) {
    if (document == null) return null;
    return document.readOnly(new IUnitOfWork<INode, XtextResource>() {
      public INode exec(XtextResource resource) throws Exception {
        IParseResult parseResult = resource.getParseResult();
        ILeafNode node = findLeafNodeAtOffset(parseResult.getRootNode(), subRegion.getOffset());
        return shouldSpellCheck(node) ? node : null;
      }
    });
  }

  @Override public void reconcile(IRegion subRegion) {
    super.reconcile(subRegion);
    initialReconcile();
  }

  @Override public void setDocument(IDocument document) {
    super.setDocument(document);
    this.document = (XtextDocument) document;
    if (spellingStrategy == null) return;
    spellingStrategy.setDocument(document);
    initialReconcile();
  }

  private void initialReconcile() {
    if (spellingStrategy == null) return;
    for (IRegion region : regionsToSpellCheck()) {
      spellingStrategy.reconcile(region);
    }
  }

  private Collection<IRegion> regionsToSpellCheck() {
    if (document == null) return emptyList();
    return document.readOnly(new IUnitOfWork<Collection<IRegion>, XtextResource>() {
      public Collection<IRegion> exec(XtextResource resource) throws Exception {
        return regionsToSpellCheck(resource.getParseResult().getRootNode());
      }
    });
  }
  
  private Collection<IRegion> regionsToSpellCheck(INode root) {
    if (!(root instanceof AbstractNode)) {
      System.out.println("is not instanceof AbstractNode: " + root);
      return emptyList();
    }
    Set<IRegion> regions = new HashSet<IRegion>();
    BidiTreeIterator<AbstractNode> iterator = ((AbstractNode) root).basicIterator();
    while (iterator.hasNext()) {
      AbstractNode next = iterator.next();
      if (next == root) continue;
      if (next instanceof ILeafNode) {
        if (shouldSpellCheck(next)) regions.add(new NodeBasedRegion(next));
        continue;
      }
      if (next.getSemanticElement() instanceof Import) continue;
      regions.addAll(regionsToSpellCheck(next));
    }
    return regions;
  }
  
  private boolean shouldSpellCheck(INode node) {
    String text = node.getText();
    if (isEmpty(text) || isEmpty(text.trim())) return false;
    return nodes.isCommentOrString(node);
  }

  void addSpellSupport(ISourceViewer viewer, SpellingService spellingService) {
    if (spellingStrategy != null) return;
    spellingStrategy = new SpellingReconcileStrategy(viewer, spellingService);
  }
}
