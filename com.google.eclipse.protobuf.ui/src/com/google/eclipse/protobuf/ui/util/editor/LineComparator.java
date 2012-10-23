/*
 * Copyright (c) 2011 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License v1.0 which
 * accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.ui.util.editor;

import org.apache.log4j.Logger;
import org.eclipse.compare.rangedifferencer.IRangeComparator;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;

/**
 * Adapted from CDT's {@code org.eclipse.cdt.internal.ui.text.LineSeparator}.
 *
 * This implementation of <code>IRangeComparator</code> compares lines of a document. The lines are compared using a DJB
 * hash function.
 *
 * @author alruiz@google.com (Alex Ruiz)
 */
class LineComparator implements IRangeComparator {
  private static final long UNKNOWN_HASH = Long.MIN_VALUE;

  private static Logger logger = Logger.getLogger(LineComparator.class);

  private final IDocument document;
  private final long[] hashes;

  public LineComparator(IDocument document) {
    this.document = document;
    hashes = new long[document.getNumberOfLines()];
    for (int i = 0; i < hashes.length; i++) {
      hashes[i] = UNKNOWN_HASH;
    }
  }

  @Override public int getRangeCount() {
    return document.getNumberOfLines();
  }

  @Override public boolean rangesEqual(int thisIndex, IRangeComparator other, int otherIndex) {
    try {
      return getHash(thisIndex) == ((LineComparator) other).getHash(otherIndex);
    } catch (BadLocationException e) {
      logger.error(e.getMessage(), e);
      return false;
    }
  }

  @Override public boolean skipRangeComparison(int length, int maxLength, IRangeComparator other) {
    return false;
  }

  /**
   * Returns the hash of the given line.
   * @param line the number of the line in the document to get the hash for.
   * @return the hash of the line.
   * @throws BadLocationException if the line number is invalid.
   */
  private int getHash(int line) throws BadLocationException {
    long hash = hashes[line];
    if (hash == UNKNOWN_HASH) {
      IRegion lineRegion = document.getLineInformation(line);
      String lineContents = document.get(lineRegion.getOffset(), lineRegion.getLength());
      hash = computeDJBHash(lineContents);
      hashes[line] = hash;
    }
    return (int) hash;
  }

  /**
   * Compute a hash using the DJB hash algorithm.
   * @param s the string for which to compute a hash.
   * @return the DJB hash value of the {@code String}.
   */
  private int computeDJBHash(String s) {
    int hash = 5381;
    int length = s.length();
    for (int i = 0; i < length; i++) {
      char ch = s.charAt(i);
      hash = (hash << 5) + hash + ch;
    }
    return hash;
  }
}
