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
import org.eclipse.xtext.naming.*;

import com.google.eclipse.protobuf.protobuf.Group;
import com.google.inject.ImplementedBy;

/**
 * @author alruiz@google.com (Alex Ruiz)
 */
@ImplementedBy(ProtobufQualifiedNameProvider.class)
public interface IProtobufQualifiedNameProvider extends IQualifiedNameProvider {
  /**
   * Returns the qualified name of the given object, to be used as the name of an option. If the given object is a
   * <code>{@link Group}</code>, this method returns the name in lower case.
   * @param e the given object.
   * @return the qualified name of the given object, to be used as the name of an option.
   */
  QualifiedName getFullyQualifiedNameForOption(EObject e);
}
