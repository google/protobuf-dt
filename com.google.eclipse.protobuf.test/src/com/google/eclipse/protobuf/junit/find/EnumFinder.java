/*
 * Copyright (c) 2011 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.junit.find;

import static org.eclipse.xtext.EcoreUtil2.getAllContentsOfType;

import com.google.eclipse.protobuf.protobuf.Enum;

/**
 * @author alruiz@google.com (Alex Ruiz)
 */
public final class EnumFinder {

  private EnumFinder() {}

  public static Enum findEnum(Name name, Root root) {
    for (Enum anEnum : getAllContentsOfType(root.value, Enum.class))
      if (name.value.equals(anEnum.getName())) return anEnum;
    return null;
  }
}
