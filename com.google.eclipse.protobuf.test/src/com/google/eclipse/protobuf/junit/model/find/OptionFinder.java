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
public final class OptionFinder {

  private OptionFinder() {}

  public static Option findOption(Name name, Root root) {
    return option(name, root, Option.class);
  }

  public static CustomOption findCustomOption(Name name, Root root) {
    return option(name, root, CustomOption.class);
  }

  private static <T extends Option> T option(Name name, Root root, Class<T> optionType) {
    for (T option : getAllContentsOfType(root.value, optionType))
      if (name.value.equals(nameOf(option))) return option;
    return null;
  }

  private static String nameOf(Option option) {
    PropertyRef ref = option.getProperty();
    if (ref == null) return null;
    Property property = ref.getProperty();
    return (property == null) ? null : property.getName();
  }
}
