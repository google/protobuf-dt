/*
 * Copyright (c) 2012 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.cdt.mapping;

import java.util.List;

import org.eclipse.emf.ecore.EClass;

/**
 * Information of the protocol buffer element obtained from a generated C++ element.
 *
 * @author alruiz@google.com (Alex Ruiz)
 */
public class CppToProtobufMapping {
  private final List<String> qualifiedName;
  private final EClass type;

  CppToProtobufMapping(List<String> qualifiedName, EClass type) {
    this.qualifiedName = qualifiedName;
    this.type = type;
  }

  /**
   * Returns the qualified name of the selected C++ element.
   * @return the qualified name of the selected C++ element.
   */
  public List<String> qualifiedName() {
    return qualifiedName;
  }

  /**
   * Returns the type of the protocol buffer element.
   * @return the type of the protocol buffer element.
   */
  public EClass type() {
    return type;
  }
}
