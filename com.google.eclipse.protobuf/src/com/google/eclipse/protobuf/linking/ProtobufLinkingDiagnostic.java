/*
 * Copyright (c) 2011 Google Inc.
 * 
 * All rights reserved. This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License v1.0 which
 * accompanies this distribution, and is available at
 * 
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.linking;

import static java.util.Arrays.copyOf;
import static org.eclipse.xtext.util.Arrays.contains;

import org.eclipse.xtext.diagnostics.AbstractDiagnostic;
import org.eclipse.xtext.nodemodel.INode;

/**
 * @author alruiz@google.com (Alex Ruiz)
 */
public class ProtobufLinkingDiagnostic extends AbstractDiagnostic {

  private final String code;
  private final String[] data;
  private final StringBuilder message;
  private final INode node;

  public ProtobufLinkingDiagnostic(String code, String[] data, String message, INode node) {
    if (contains(data, null)) {
      throw new NullPointerException("data may not contain null");
    }
    if (node == null) throw new NullPointerException("node may not be null");
    this.code = code;
    this.data = copyOf(data, data.length);
    this.message = new StringBuilder();
    this.message.append(message);
    this.node = node;
  }

  @Override public String getCode() {
    return code;
  }

  @Override public String[] getData() {
    return copyOf(data, data.length);
  }

  @Override public String getMessage() {
    return message.toString();
  }

  @Override protected INode getNode() {
    return node;
  }

  public void appendToMessage(String s) {
    message.append(s);
  }
  
  @Override public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((message == null) ? 0 : message.hashCode());
    result = prime * result + ((node == null) ? 0 : node.hashCode());
    return result;
  }

  @Override public boolean equals(Object obj) {
    if (this == obj) return true;
    if (obj == null) return false;
    if (getClass() != obj.getClass()) return false;
    ProtobufLinkingDiagnostic other = (ProtobufLinkingDiagnostic) obj;
    if (message == null) {
      if (other.message != null) return false;
    } else if (!message.equals(other.message)) return false;
    if (node == null) {
      if (other.node != null) return false;
    } else if (!node.equals(other.node)) return false;
    return true;
  }
}
