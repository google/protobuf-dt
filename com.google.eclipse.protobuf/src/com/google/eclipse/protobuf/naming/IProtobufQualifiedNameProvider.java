/*
 * Copyright (c) 2011 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.naming;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.xtext.naming.IQualifiedNameProvider;
import org.eclipse.xtext.naming.QualifiedName;

import com.google.inject.ImplementedBy;

/**
 * @author alruiz@google.com (Alex Ruiz)
 */
@ImplementedBy(ProtobufQualifiedNameProvider.class)
public interface IProtobufQualifiedNameProvider extends IQualifiedNameProvider {
  /**
   * Returns the qualified name of the given model object.
   * @param e the given model object.
   * @param strategy gets the name of the model object.
   * @return the qualified name of the given model object.
   */
  QualifiedName getFullyQualifiedName(EObject e, NamingStrategy strategy);
}
