/*
 * Copyright (c) 2011 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.model.util;

import static java.util.Collections.unmodifiableList;

import static com.google.common.collect.Lists.newArrayList;

import java.util.List;

import com.google.eclipse.protobuf.parser.NonProto2Protobuf;
import com.google.eclipse.protobuf.protobuf.Import;
import com.google.eclipse.protobuf.protobuf.Protobuf;
import com.google.eclipse.protobuf.protobuf.ProtobufElement;
import com.google.eclipse.protobuf.protobuf.PublicImport;
import com.google.inject.Singleton;

/**
 * Utility methods related to <code>{@link Protobuf}</code>s.
 *
 * @author alruiz@google.com (Alex Ruiz)
 */
@Singleton public class Protobufs {
  /**
   * Indicates whether the given root is not {@code null} and has a "proto2" syntax element.
   * @param protobuf the given root.
   * @return {@code true} if the given root is not {@code null} and has a "proto2" syntax element, {@code false}
   * otherwise.
   */
  public boolean isProto2(Protobuf protobuf) {
    return protobuf != null && !(protobuf instanceof NonProto2Protobuf);
  }

  /**
   * Returns all the import definitions in the given root.
   * @param root the given root.
   * @return all the import definitions in the given root.
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
   * Returns all the public import definitions in the given root.
   * @param root the given root.
   * @return all the public import definitions in the given root.
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
