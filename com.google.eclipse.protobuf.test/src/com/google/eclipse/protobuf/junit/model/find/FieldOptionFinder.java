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
    for (FieldOption option : getAllContentsOfType(root.value, FieldOption.class))
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
