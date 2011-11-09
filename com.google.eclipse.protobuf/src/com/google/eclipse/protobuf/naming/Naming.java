/*
 * Copyright (c) 2011 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.naming;

import static org.eclipse.xtext.util.SimpleAttributeResolver.newResolver;

import com.google.common.base.Function;
import com.google.eclipse.protobuf.model.util.Options;
import com.google.eclipse.protobuf.protobuf.*;
import com.google.inject.*;

import org.eclipse.emf.ecore.EObject;

/**
 * Utility methods related to naming.
 * 
 * @author alruiz@google.com (Alex Ruiz)
 */
@Singleton
public class Naming {

  @Inject private Options options;

  private final Function<EObject, String> resolver = newResolver(String.class, "name");

  /**
   * Returns the name of the given object. If the name target is an option and the given object is a 
   * <code>{@link Group}</code>, this method will return the name of the group in lower case.
   * @param e the given object.
   * @param target the name target.
   * @return the name of the given object.
   */
  public String nameOf(EObject e, NameTarget target) {
    if (NameTarget.TYPE.equals(target)) return resolver.apply(e);
    return (e instanceof IndexedElement) ? options.nameForOption((IndexedElement) e) : resolver.apply(e);
  }

  /**
   * Indicates if the name to obtain will be used by a type or an option.
   * 
   * @author alruiz@google.com (Alex Ruiz)
   */
  public static enum NameTarget {
    TYPE, OPTION;
  }
}
