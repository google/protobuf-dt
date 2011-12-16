/*
 * Copyright (c) 2011 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.linking;

import static com.google.eclipse.protobuf.util.Objects.*;
import static java.util.Arrays.copyOf;
import static org.eclipse.xtext.util.Arrays.contains;

import org.eclipse.xtext.diagnostics.*;
import org.eclipse.xtext.nodemodel.INode;

/**
 * <code>{@link Diagnostic}</code> that supports appending text to its message.
 *
 * @author alruiz@google.com (Alex Ruiz)
 */
public class ProtobufDiagnostic extends AbstractDiagnostic {

  private final String code;
  private final String[] data;
  private final StringBuilder message;
  private final INode node;

  public ProtobufDiagnostic(String code, String[] data, String message, INode node) {
    validate(data);
    if (node == null) {
      throw new NullPointerException("node should not be null");
    }
    this.code = code;
    this.data = copyOf(data, data.length);
    this.message = new StringBuilder();
    this.message.append(message);
    this.node = node;
  }

  private static void validate(String[] data) {
    if (data == null) {
      throw new NullPointerException("data should not be a null array");
    }
    if (contains(data, null)) {
      throw new NullPointerException("data should not contain null");
    }
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

  /**
   * Appends the given text to the message of this diagnostic.
   * @param s the text to append.
   */
  public void appendToMessage(String s) {
    message.append(s);
  }

  @Override public int hashCode() {
    final int prime = HASH_CODE_PRIME;
    int result = 1;
    result = prime * result + hashCodeOf(message);
    result = prime * result + hashCodeOf(node);
    return result;
  }

  @Override public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null || getClass() != obj.getClass()) {
      return false;
    }
    ProtobufDiagnostic other = (ProtobufDiagnostic) obj;
    if (!areEqual(message, other.message)) {
      return false;
    }
    return areEqual(node, other.node);
  }
}
