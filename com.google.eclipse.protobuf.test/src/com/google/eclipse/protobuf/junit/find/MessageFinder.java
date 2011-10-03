/*
 * Copyright (c) 2011 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.junit.find;

import static org.eclipse.xtext.EcoreUtil2.getAllContentsOfType;

import com.google.eclipse.protobuf.protobuf.Message;

/**
 * @author alruiz@google.com (Alex Ruiz)
 */
public final class MessageFinder {

  public static Message findMessage(Name name, Root root) {
    for (Message message : getAllContentsOfType(root.value, Message.class))
      if (name.value.equals(message.getName())) return message;
    return null;
  }

  private MessageFinder() {}
}
