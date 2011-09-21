/*
 * Copyright (c) 2011 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.scoping;

import java.util.*;
import java.util.Map.Entry;

import org.eclipse.emf.ecore.EObject;

import com.google.eclipse.protobuf.protobuf.*;
import com.google.eclipse.protobuf.protobuf.Enum;

/**
 * @author alruiz@google.com (Alex Ruiz)
 */
enum OptionType {
  FILE("FileOptions"), MESSAGE("MessageOptions"), FIELD("FieldOptions"), ENUM("EnumOptions"),
      ENUM_LITERAL("EnumValueOptions"), SERVICE("ServiceOptions"), RPC("MethodOptions");

  private static final Map<Class<?>, OptionType> OPTION_TYPES_BY_CONTAINER = new HashMap<Class<?>, OptionType>();

  static {
    OPTION_TYPES_BY_CONTAINER.put(Protobuf.class, FILE);
    OPTION_TYPES_BY_CONTAINER.put(Enum.class, ENUM);
    OPTION_TYPES_BY_CONTAINER.put(Message.class, MESSAGE);
    OPTION_TYPES_BY_CONTAINER.put(Service.class, SERVICE);
    OPTION_TYPES_BY_CONTAINER.put(Rpc.class, RPC);
  }

  final String messageName;

  private OptionType(String messageName) {
    this.messageName = messageName;
  }

  static OptionType optionType(CustomOption option) {
    EObject container = option.eContainer();
    for (Entry<Class<?>, OptionType> optionTypeByContainer : OPTION_TYPES_BY_CONTAINER.entrySet()) {
      if (optionTypeByContainer.getKey().isInstance(container)) {
        return optionTypeByContainer.getValue();
      }
    }
    return null;
  }
}