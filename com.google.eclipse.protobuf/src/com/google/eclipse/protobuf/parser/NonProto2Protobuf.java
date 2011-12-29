/*
 * Copyright (c) 2011 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.parser;

import com.google.eclipse.protobuf.protobuf.impl.ProtobufImpl;

/**
 * Represents a non-proto2 protocol buffer, which is ignored by the editor.
 *
 * @author alruiz@google.com (Alex Ruiz)
 */
public class NonProto2Protobuf extends ProtobufImpl {

  /**
   * Creates a new <code>{@link NonProto2Protobuf}</code>.
   */
  public NonProto2Protobuf() {}
}
