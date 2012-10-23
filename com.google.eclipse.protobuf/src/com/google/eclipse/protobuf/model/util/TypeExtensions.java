/*
 * Copyright (c) 2011 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.model.util;

import com.google.eclipse.protobuf.protobuf.ExtensibleType;
import com.google.eclipse.protobuf.protobuf.ExtensibleTypeLink;
import com.google.eclipse.protobuf.protobuf.Message;
import com.google.eclipse.protobuf.protobuf.TypeExtension;

/**
 * Utility methods related to <code>{@link TypeExtension}</code>s.
 *
 * @author alruiz@google.com (Alex Ruiz)
 */
public class TypeExtensions {
  /**
   * Returns the message from the given extension.
   * @param extension the given extension.
   * @return the message from the given extension, or {@code null} if the extension is not referring to a message.
   */
  public Message messageFrom(TypeExtension extension) {
    ExtensibleTypeLink link = extension.getType();
    if (link == null) {
      return null;
    }
    ExtensibleType type = link.getTarget();
    return (type instanceof Message) ? (Message) type : null;
  }
}
