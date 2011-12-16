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

import com.google.eclipse.protobuf.model.util.INodes;
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

  @Inject private EObjectAtOffsetHelper eObjectAtOffsetHelper;
  @Inject private FileOpener fileOpener;
  @Inject private INodes nodes;

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
        String importUri = rawUriIn(anImport);
        if (importUri == null) {
          return NO_HYPERLINKS;
        }
        IRegion importUriRegion;
        try {
          importUriRegion = importUriRegion(document, region.getOffset(), importUri);
        } catch (BadLocationException e) {
          return NO_HYPERLINKS;
        }
        if (importUriRegion == null) {
          return NO_HYPERLINKS;
        }
        IHyperlink hyperlink = new ImportHyperlink(createURI(anImport.getImportURI()), importUriRegion, fileOpener);
        return new IHyperlink[] { hyperlink };
      }
    });
  }

  private String rawUriIn(Import anImport) {
    INode node = nodes.firstNodeForFeature(anImport, IMPORT__IMPORT_URI);
    if (node == null) {
      return null;
    }
    String text = node.getText();
    if (text == null || text.length() < 3) {
      return null;
    }
    return text.substring(1, text.length() - 1); // remove quotes
  }

  private IRegion importUriRegion(IXtextDocument document, int offset, String importUri) throws BadLocationException {
    int lineNumber = document.getLineOfOffset(offset);
    int lineLength = document.getLineLength(lineNumber);
    int lineOffset = document.getLineOffset(lineNumber);
    String line = document.get(lineOffset, lineLength);
    int uriIndex = line.indexOf(importUri);
    return new Region(lineOffset + uriIndex, importUri.length());
  }
}
