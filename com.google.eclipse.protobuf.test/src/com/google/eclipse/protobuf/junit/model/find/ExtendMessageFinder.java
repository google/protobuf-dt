/*
 * Copyright (c) 2011 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.junit.model.find;

import static org.eclipse.xtext.EcoreUtil2.getAllContentsOfType;

import com.google.eclipse.protobuf.protobuf.*;

/**
 * @author alruiz@google.com (Alex Ruiz)
 */
public final class ExtendMessageFinder {

  public static ExtendMessage findExtendMessage(Name name, Root root) {
    for (ExtendMessage extend : getAllContentsOfType(root.value, ExtendMessage.class))
      if (name.value.equals(nameOf(extend))) return extend;
    return null;
  }
  
  private static String nameOf(ExtendMessage extend) {
    MessageRef ref = extend.getMessage();
    if (ref == null) return null;
    Message message = ref.getType();
    return (message != null) ? message.getName() : null;
  }

  private ExtendMessageFinder() {}
}
