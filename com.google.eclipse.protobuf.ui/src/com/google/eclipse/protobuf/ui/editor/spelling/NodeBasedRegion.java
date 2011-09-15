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

import org.eclipse.jface.text.IRegion;
import org.eclipse.xtext.nodemodel.INode;

class NodeBasedRegion implements IRegion {
  
  private final int length;
  private final int offset;
  private final String text;

  NodeBasedRegion(INode node) {
    length = node.getTotalLength();
    offset = node.getTotalOffset();
    text = node.getText();
  }

  public int getLength() {
    return length;
  }

  public int getOffset() {
    return offset;
  }

  @Override public boolean equals(Object obj) {
    if (this == obj) return true;
    if (obj == null) return false;
    if (getClass() != obj.getClass()) return false;
    NodeBasedRegion other = (NodeBasedRegion) obj;
    if (length != other.length) return false;
    return offset == other.offset;
  }

  @Override public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + length;
    result = prime * result + offset;
    return result;
  }

  @Override public String toString() {
    String format = getClass().getSimpleName() + " [length=%s, offset=%s, text=%s]";
    return String.format(format, length, offset, quote(text));
  }
  
  private String quote(String s) {
    if (s == null) return s;
    return "'" + s + "'";
  }
}