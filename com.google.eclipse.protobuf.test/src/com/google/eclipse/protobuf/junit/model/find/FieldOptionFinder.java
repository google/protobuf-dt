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
public final class FieldOptionFinder {

  public static FieldOption findFieldOption(Name name, Root root) {
    return fieldOption(name, root, FieldOption.class);
  }
  
  public static NativeFieldOption findNativeFieldOption(Name name, Root root) {
    return fieldOption(name, root, NativeFieldOption.class);
  }

  public static CustomFieldOption findCustomFieldOption(Name name, Root root) {
    return fieldOption(name, root, CustomFieldOption.class);
  }

  private static <T extends FieldOption> T fieldOption(Name name, Root root, Class<T> optionType) {
    for (T option : getAllContentsOfType(root.value, optionType))
      if (name.value.equals(nameOf(option))) return option;
    return null;
  }

  private static String nameOf(FieldOption option) {
    if (option instanceof DefaultValueFieldOption) return "default";
    PropertyRef ref = null;
    if (option instanceof NativeFieldOption) {
      ref = ((NativeFieldOption) option).getProperty();
    }
    if (option instanceof CustomFieldOption) {
      ref = ((CustomFieldOption) option).getProperty();
    }
    Property property = ref.getProperty();
    return (property == null) ? null : property.getName();
  }

  private FieldOptionFinder() {}
}
