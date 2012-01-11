/*
 * Copyright (c) 2012 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.ui.commands.semicolon;

import org.eclipse.swt.custom.StyledText;

/**
 * @author alruiz@google.com (Alex Ruiz)
 */
class StyledTextAccess {
  private final StyledText styledText;

  StyledTextAccess(StyledText styledText) {
    this.styledText = styledText;
  }

  String lineAtCaretOffset() {
    int offset = caretOffset();
    int lineAtOffset = styledText.getLineAtOffset(offset);
    return styledText.getLine(lineAtOffset);
  }

  void setCaretOffsetToEndOfLine() {
    int offset = caretOffset();
    int lineAtOffset = styledText.getLineAtOffset(offset);
    String line = styledText.getLine(lineAtOffset);
    int offsetAtLine = styledText.getOffsetAtLine(lineAtOffset);
    offset = offsetAtLine + line.length();
    styledText.setCaretOffset(offset);
  }

  void insert(String text) {
    styledText.insert(text);
    styledText.setCaretOffset(caretOffset() + text.length());
  }

  int caretOffset() {
    return styledText.getCaretOffset();
  }
}
