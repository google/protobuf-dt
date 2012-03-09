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
import static org.eclipse.emf.common.util.URI.createURI;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.jface.text.*;
import org.eclipse.jface.text.hyperlink.*;
import org.eclipse.xtext.CrossReference;
import org.eclipse.xtext.nodemodel.INode;
import org.eclipse.xtext.resource.*;
import org.eclipse.xtext.ui.editor.hyperlinking.DefaultHyperlinkDetector;
import org.eclipse.xtext.ui.editor.model.IXtextDocument;
import org.eclipse.xtext.util.concurrent.IUnitOfWork;

import com.google.eclipse.protobuf.model.util.*;
import com.google.eclipse.protobuf.protobuf.Import;
import com.google.eclipse.protobuf.ui.editor.FileOpener;
import com.google.inject.Inject;

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
  @Inject private FileOpener fileOpener;
  @Inject private Imports imports;

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
        int importUriLength = importUriNode.getLength();
        if (importUriLength == 0) {
          return NO_HYPERLINKS;
        }
        IRegion importUriRegion = new Region(importUriNode.getOffset(), importUriLength);
        IHyperlink hyperlink = new ImportHyperlink(createURI(anImport.getImportURI()), importUriRegion, fileOpener);
        return new IHyperlink[] { hyperlink };
      }
    });
  }
}
