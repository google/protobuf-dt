/*
 * Copyright (c) 2011 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.scoping;

/**
 * @author alruiz@google.com (Alex Ruiz)
 */
enum OptionType {
  FILE("FileOptions"), MESSAGE("MessageOptions"), FIELD("FieldOptions"), ENUM("EnumOptions"),
      LITERAL("EnumValueOptions"), SERVICE("ServiceOptions"), RPC("MethodOptions");

  final String messageName;

  private OptionType(String messageName) {
    this.messageName = messageName;
  }
}