/*
 * Copyright (c) 2012 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.cdt.mapping;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.xtext.naming.QualifiedName;

/**
 * Information of the protocol buffer element obtained from a generated C++ element.
 *
 * @author alruiz@google.com (Alex Ruiz)
 */
public class CppToProtobufMapping {
  private final QualifiedName qualifiedName;
  private final Class<? extends EObject> type;

  public CppToProtobufMapping(QualifiedName qualifiedName, Class<? extends EObject> type) {
    this.qualifiedName = qualifiedName;
    this.type = type;
  }

  /**
   * Returns the possible qualified name of the protocol buffer element.
   * @return the possible qualified name of the protocol buffer element.
   */
  public QualifiedName qualifiedName() {
    return qualifiedName;
  }

  /**
   * Returns the type of the protocol buffer element.
   * @return the type of the protocol buffer element.
   */
  public Class<? extends EObject> type() {
    return type;
  }
}
