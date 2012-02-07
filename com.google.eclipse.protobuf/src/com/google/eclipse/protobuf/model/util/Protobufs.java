/*
 * Copyright (c) 2011 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.model.util;

import static com.google.common.collect.Lists.newArrayList;
import static java.util.Collections.unmodifiableList;

import java.util.List;

import com.google.eclipse.protobuf.parser.NonProto2Protobuf;
import com.google.eclipse.protobuf.protobuf.*;
import com.google.inject.Singleton;

/**
 * Utility methods related to <code>{@link Protobuf}</code>s.
 *
 * @author alruiz@google.com (Alex Ruiz)
 */
@Singleton public class Protobufs {
  /**
   * Indicates whether the given <code>{@link Protobuf}</code> is not {@code null} and has "proto2" syntax (not
   * necessarily in a explicit way.)
   * @param protobuf the {@code Protobuf} to check.
   * @return {@code true} if the given <code>{@link Protobuf}</code> is not {@code null} and has "proto2" syntax,
   * {@code false} otherwise.
   */
  public boolean isProto2(Protobuf protobuf) {
    return protobuf != null && !(protobuf instanceof NonProto2Protobuf);
  }

  /**
   * Returns all the import definitions in the given proto.
   * @param root the given proto.
   * @return all the import definitions in the given proto.
   */
  public List<Import> importsIn(Protobuf root) {
    List<Import> imports = newArrayList();
    for (ProtobufElement e : root.getElements()) {
      if (e instanceof Import) {
        imports.add((Import) e);
      }
    }
    return unmodifiableList(imports);
  }

  /**
   * Returns all the public import definitions in the given proto.
   * @param root the given proto.
   * @return all the public import definitions in the given proto.
   */
  public List<Import> publicImportsIn(Protobuf root) {
    List<Import> imports = newArrayList();
    for (ProtobufElement e : root.getElements()) {
      if (e instanceof PublicImport) {
        imports.add((Import) e);
      }
    }
    return unmodifiableList(imports);
  }
}
