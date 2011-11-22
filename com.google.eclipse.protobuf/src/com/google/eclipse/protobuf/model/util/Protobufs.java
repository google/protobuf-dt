/*
 * Copyright (c) 2011 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.model.util;

import com.google.eclipse.protobuf.parser.NonProto2Protobuf;
import com.google.eclipse.protobuf.protobuf.Protobuf;
import com.google.inject.Singleton;

/**
 * Utility methods related to <code>{@link Protobuf}</code>s.
 *
 * @author alruiz@google.com (Alex Ruiz)
 */
@Singleton
public class Protobufs {

  /**
   * Indicates whether the given <code>{@link Protobuf}</code> is not {@code null} and has "proto2" syntax.
   * @param protobuf the {@code Protobuf} to check.
   * @return {@code true} if the given <code>{@link Protobuf}</code> is not {@code null} and has "proto2" syntax,
   * {@code false} otherwise.
   */
  public boolean isProto2(Protobuf protobuf) {
    return protobuf != null && !(protobuf instanceof NonProto2Protobuf);
  }
}
