/*
 * Copyright (c) 2011 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.scoping;

import static com.google.common.collect.Maps.newHashMap;

import java.util.Map;
import java.util.Map.Entry;

import com.google.eclipse.protobuf.protobuf.AbstractOption;
import com.google.eclipse.protobuf.protobuf.Enum;
import com.google.eclipse.protobuf.protobuf.Group;
import com.google.eclipse.protobuf.protobuf.IndexedElement;
import com.google.eclipse.protobuf.protobuf.Literal;
import com.google.eclipse.protobuf.protobuf.Message;
import com.google.eclipse.protobuf.protobuf.Protobuf;
import com.google.eclipse.protobuf.protobuf.Rpc;
import com.google.eclipse.protobuf.protobuf.Service;
import com.google.eclipse.protobuf.protobuf.Stream;

/**
 * Types of options (by location.)
 *
 * @author alruiz@google.com (Alex Ruiz)
 */
enum OptionType {
  FILE("FileOptions"), MESSAGE("MessageOptions"), FIELD("FieldOptions"), ENUM("EnumOptions"),
  LITERAL("EnumValueOptions"), SERVICE("ServiceOptions"), RPC("MethodOptions"), STREAM("StreamOptions");

  private static final Map<Class<?>, OptionType> OPTION_TYPES_BY_CONTAINER = newHashMap();

  static {
    OPTION_TYPES_BY_CONTAINER.put(Protobuf.class, FILE);
    OPTION_TYPES_BY_CONTAINER.put(Enum.class, ENUM);
    OPTION_TYPES_BY_CONTAINER.put(Literal.class, LITERAL);
    OPTION_TYPES_BY_CONTAINER.put(Message.class, MESSAGE);
    OPTION_TYPES_BY_CONTAINER.put(IndexedElement.class, FIELD);
    OPTION_TYPES_BY_CONTAINER.put(Service.class, SERVICE);
    OPTION_TYPES_BY_CONTAINER.put(Rpc.class, RPC);
    OPTION_TYPES_BY_CONTAINER.put(Stream.class, STREAM);
  }

  // The name of the message in descriptor.proto that specifies the type of an option.
  private final String messageName;

  private OptionType(String messageName) {
    this.messageName = messageName;
  }

  /**
   * Returns the name of the message in descriptor.proto that specifies the type of an option.
   * @return the name of the message in descriptor.proto that specifies the type of an option.
   */
  String messageName() {
    return messageName;
  }

  /**
   * Returns the type of the given option.
   * @param option the given option.
   * @return the type of the given option or {@code null} if a type cannot be found.
   */
  static OptionType typeOf(AbstractOption option) {
    return findOptionTypeForLevelOf(option.eContainer());
  }

  static OptionType findOptionTypeForLevelOf(Object container) {
    for (Entry<Class<?>, OptionType> optionTypeByContainer : OPTION_TYPES_BY_CONTAINER.entrySet()) {
      if (optionTypeByContainer.getKey().isInstance(container)) {
        return optionTypeByContainer.getValue();
      }
    }

    return null;
  }
}
