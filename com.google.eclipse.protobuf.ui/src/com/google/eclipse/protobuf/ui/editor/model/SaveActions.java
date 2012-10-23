/*
 * Copyright (c) 2011 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License v1.0 which
 * accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.ui.editor.model;

import static java.lang.Character.isWhitespace;

import static org.eclipse.jface.text.IDocumentExtension3.DEFAULT_PARTITIONING;
import static org.eclipse.jface.text.TextUtilities.getPartition;

import org.apache.log4j.Logger;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITypedRegion;
import org.eclipse.text.edits.DeleteEdit;
import org.eclipse.text.edits.MultiTextEdit;
import org.eclipse.text.edits.TextEdit;

import com.google.inject.Singleton;

/**
 * @author alruiz@google.com (Alex Ruiz)
 */
@Singleton class SaveActions {
  private static Logger logger = Logger.getLogger(SaveActions.class);

  TextEdit createSaveAction(IDocument document, IRegion[] changedRegions) {
    try {
      return doCreateSaveAction(document, changedRegions);
    } catch (BadLocationException e) {
      logger.error("Unable to create save actions", e);
    }
    return null;
  }

  private TextEdit doCreateSaveAction(IDocument document, IRegion[] changedRegions) throws BadLocationException {
    TextEdit rootEdit = null;
    for (IRegion region : changedRegions) {
      int lastLine = document.getLineOfOffset(region.getOffset() + region.getLength());
      for (int line = firstLine(region, document); line <= lastLine; line++) {
        IRegion lineRegion = document.getLineInformation(line);
        if (lineRegion.getLength() == 0) {
          continue;
        }
        int lineStart = lineRegion.getOffset();
        int lineEnd = lineStart + lineRegion.getLength();
        int charPos = rightMostNonWhitespaceChar(document, lineStart, lineEnd);
        if (charPos >= lineEnd) {
          continue;
        }
        // check partition - don't remove whitespace inside strings
        ITypedRegion partition = getPartition(document, DEFAULT_PARTITIONING, charPos, false);
        if ("__string".equals(partition.getType())) {
          continue;
        }
        if (rootEdit == null) {
          rootEdit = new MultiTextEdit();
        }
        rootEdit.addChild(new DeleteEdit(charPos, lineEnd - charPos));
      }
    }
    return rootEdit;
  }

  private int firstLine(IRegion region, IDocument document) throws BadLocationException {
    return document.getLineOfOffset(region.getOffset());
  }

  private int rightMostNonWhitespaceChar(IDocument document, int lineStart, int lineEnd) throws BadLocationException {
    int charPos = lineEnd - 1;
    while (charPos >= lineStart && isWhitespace(document.getChar(charPos))) {
      charPos--;
    }
    return ++charPos;
  }
}
