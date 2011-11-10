// Copyright 2011 Google Inc. All Rights Reserved.

package com.google.eclipse.protobuf.naming;

import com.google.eclipse.protobuf.protobuf.Group;
import com.google.inject.ImplementedBy;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.xtext.naming.*;

/**
 * @author alruiz@google.com (Alex Ruiz)
 */
@ImplementedBy(ProtobufQualifiedNameProvider.class)
public interface IProtobufQualifiedNameProvider extends IQualifiedNameProvider {

  /**
   * Returns the qualified name of the given object, to be used in an option. If the given object is a 
   * <code>{@link Group}</code>, this methods returns the name in lower case.
   * @param e the given object.
   * @return the qualified name of the given object, to be used in an option.
   */
  QualifiedName getFullyQualifiedNameForOption(EObject e);
}
