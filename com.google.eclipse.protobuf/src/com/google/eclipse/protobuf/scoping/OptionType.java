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
 * Types of options (by location.)
 *
 * @author alruiz@google.com (Alex Ruiz)
 */
enum OptionType {

  // TODO move back to package "com.google.eclipse.protobuf.scoping" and make it package-protected.

  FILE("FileOptions"), MESSAGE("MessageOptions"), FIELD("FieldOptions"), ENUM("EnumOptions"),
      LITERAL("EnumValueOptions"), SERVICE("ServiceOptions"), RPC("MethodOptions");

  private static final Map<Class<?>, OptionType> OPTION_TYPES_BY_CONTAINER = new HashMap<Class<?>, OptionType>();

  static {
    OPTION_TYPES_BY_CONTAINER.put(Protobuf.class, FILE);
    OPTION_TYPES_BY_CONTAINER.put(Enum.class, ENUM);
    OPTION_TYPES_BY_CONTAINER.put(Message.class, MESSAGE);
    OPTION_TYPES_BY_CONTAINER.put(Property.class, FIELD);
    OPTION_TYPES_BY_CONTAINER.put(Service.class, SERVICE);
    OPTION_TYPES_BY_CONTAINER.put(Rpc.class, RPC);
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
  static OptionType typeOf(FieldOption option) {
    return findType(option.eContainer());
  }

  /**
   * Returns the type of the given option.
   * @param option the given option.
   * @return the type of the given option or {@code null} if a type cannot be found.
   */
  static OptionType typeOf(Option option) {
    return findType(option.eContainer());
  }

  private static OptionType findType(EObject container) {
    for (Entry<Class<?>, OptionType> optionTypeByContainer : OPTION_TYPES_BY_CONTAINER.entrySet()) {
      if (optionTypeByContainer.getKey().isInstance(container)) {
        return optionTypeByContainer.getValue();
      }
    }
    return null;
  }
}
