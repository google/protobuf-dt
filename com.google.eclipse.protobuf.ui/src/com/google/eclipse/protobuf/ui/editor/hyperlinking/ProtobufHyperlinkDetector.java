/*
 * Copyright (c) 2011 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.ui.editor.hyperlinking;

import static com.google.eclipse.protobuf.protobuf.ProtobufPackage.Literals.IMPORT__IMPORT_URI;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.hyperlink.IHyperlink;
import org.eclipse.jface.text.hyperlink.IHyperlinkDetector;
import org.eclipse.xtext.CrossReference;
import org.eclipse.xtext.nodemodel.INode;
import org.eclipse.xtext.resource.EObjectAtOffsetHelper;
import org.eclipse.xtext.resource.XtextResource;
import org.eclipse.xtext.ui.editor.hyperlinking.DefaultHyperlinkDetector;
import org.eclipse.xtext.ui.editor.model.IXtextDocument;
import org.eclipse.xtext.util.concurrent.IUnitOfWork;

import com.google.eclipse.protobuf.model.util.INodes;
import com.google.eclipse.protobuf.model.util.Imports;
import com.google.eclipse.protobuf.protobuf.Import;
import com.google.inject.Inject;
import com.google.inject.Provider;

/**
 * Represents an implementation of interface <code>{@link IHyperlinkDetector}</code> to find and convert
 * {@link CrossReference elements}, at a given location, to {@code IHyperlink}.
 *
 * @author alruiz@google.com (Alex Ruiz)
 */
public class ProtobufHyperlinkDetector extends DefaultHyperlinkDetector {
  private static final IHyperlink[] NO_HYPERLINKS = null;

  @Inject private INodes nodes;
  @Inject private EObjectAtOffsetHelper eObjectAtOffsetHelper;
  @Inject private Imports imports;
  @Inject private Provider<ImportHyperlink> importHyperlinkProvider;

  @Override public IHyperlink[] detectHyperlinks(ITextViewer textViewer, final IRegion region,
      final boolean canShowMultipleHyperlinks) {
    IXtextDocument document = (IXtextDocument)textViewer.getDocument();
    IHyperlink[] importHyperlinks = importHyperlinks(document, region);
    if (importHyperlinks != NO_HYPERLINKS) {
      return importHyperlinks;
    }
    return document.readOnly(new IUnitOfWork<IHyperlink[], XtextResource>() {
      @Override public IHyperlink[] exec(XtextResource resource) {
        return getHelper().createHyperlinksByOffset(resource, region.getOffset(), canShowMultipleHyperlinks);
      }
    });
  }

  private IHyperlink[] importHyperlinks(final IXtextDocument document, final IRegion region) {
    return document.readOnly(new IUnitOfWork<IHyperlink[], XtextResource>() {
      @Override public IHyperlink[] exec(XtextResource resource) {
        EObject resolved = eObjectAtOffsetHelper.resolveElementAt(resource, region.getOffset());
        if (!(resolved instanceof Import)) {
          return NO_HYPERLINKS;
        }
        Import anImport = (Import) resolved;
        if (!imports.isResolved(anImport)) {
          return NO_HYPERLINKS;
        }
        INode importUriNode = nodes.firstNodeForFeature(anImport, IMPORT__IMPORT_URI);
        if (importUriNode.getLength() == 0) {
          return NO_HYPERLINKS;
        }
        ImportHyperlink hyperlink = importHyperlinkProvider.get();
        hyperlink.update(anImport, importUriNode);
        return new IHyperlink[] { hyperlink };
      }
    });
  }
}
