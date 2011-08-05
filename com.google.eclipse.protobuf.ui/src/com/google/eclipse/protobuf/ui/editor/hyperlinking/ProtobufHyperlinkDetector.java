/*
 * Copyright (c) 2011 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.ui.editor.hyperlinking;

import static org.eclipse.emf.common.util.URI.createURI;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.jface.text.*;
import org.eclipse.jface.text.hyperlink.IHyperlink;
import org.eclipse.jface.text.hyperlink.IHyperlinkDetector;
import org.eclipse.xtext.CrossReference;
import org.eclipse.xtext.resource.EObjectAtOffsetHelper;
import org.eclipse.xtext.resource.XtextResource;
import org.eclipse.xtext.ui.editor.hyperlinking.DefaultHyperlinkDetector;
import org.eclipse.xtext.ui.editor.model.IXtextDocument;
import org.eclipse.xtext.util.concurrent.IUnitOfWork;

import com.google.eclipse.protobuf.protobuf.Import;
import com.google.eclipse.protobuf.ui.util.Resources;
import com.google.eclipse.protobuf.util.Imports;
import com.google.inject.Inject;

/**
 * Represents an implementation of interface <code>{@link IHyperlinkDetector}</code> to find and convert
 * {@link CrossReference elements}, at a given location, to {@code IHyperlink}.
 *
 * @author alruiz@google.com (Alex Ruiz)
 */
public class ProtobufHyperlinkDetector extends DefaultHyperlinkDetector {

  private static final IHyperlink[] NO_HYPERLINKS = null;

  private static final char QUOTE = '\"';

  @Inject private EObjectAtOffsetHelper eObjectAtOffsetHelper;
  @Inject private Imports imports;
  @Inject private Resources resources;

  @Override public IHyperlink[] detectHyperlinks(ITextViewer textViewer, final IRegion region,
      final boolean canShowMultipleHyperlinks) {
    IXtextDocument document = (IXtextDocument)textViewer.getDocument();
    IHyperlink[] importHyperlinks = importHyperlinks(document, region);
    if (importHyperlinks != NO_HYPERLINKS) return importHyperlinks;
    return document.readOnly(new IUnitOfWork<IHyperlink[], XtextResource>() {
      public IHyperlink[] exec(XtextResource resource) {
        return getHelper().createHyperlinksByOffset(resource, region.getOffset(), canShowMultipleHyperlinks);
      }
    });
  }

  private IHyperlink[] importHyperlinks(final IXtextDocument document, final IRegion region) {
    return document.readOnly(new IUnitOfWork<IHyperlink[], XtextResource>() {
      public IHyperlink[] exec(XtextResource resource) {
        EObject resolved = eObjectAtOffsetHelper.resolveElementAt(resource, region.getOffset());
        if (!(resolved instanceof Import)) return NO_HYPERLINKS;
        Import anImport = (Import) resolved;
        if (imports.isImportingProtoDescriptor(anImport)) return NO_HYPERLINKS;
        IRegion importUriRegion;
        try {
          importUriRegion = importUriRegion(document, region.getOffset());
        } catch (BadLocationException e) {
          return NO_HYPERLINKS;
        }
        String importUri = anImport.getImportURI();
        IHyperlink hyperlink = new ImportHyperlink(createURI(importUri), importUriRegion, resources);
        return new IHyperlink[] { hyperlink };
      }
    });
  }

  private IRegion importUriRegion(final IXtextDocument document, final int offset) throws BadLocationException {
    int lineNumber = document.getLineOfOffset(offset);
    int lineLength = document.getLineLength(lineNumber);
    int lineOffset = document.getLineOffset(lineNumber);
    String line = document.get(lineOffset, lineLength);
    int openingQuoteIndex = line.indexOf(QUOTE);
    int closingQuoteIndex = line.indexOf(QUOTE, ++openingQuoteIndex);
    String importUri = line.substring(openingQuoteIndex, closingQuoteIndex);
    return new Region(lineOffset + openingQuoteIndex, importUri.length());
  }
}
