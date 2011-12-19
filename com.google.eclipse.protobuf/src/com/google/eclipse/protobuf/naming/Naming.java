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

import com.google.eclipse.protobuf.model.util.Options;
import com.google.eclipse.protobuf.protobuf.*;
import com.google.inject.*;

/**
 * Utility methods related to naming.
 *
 * @author alruiz@google.com (Alex Ruiz)
 */
@Singleton public class Naming {

  @Inject private NameResolver nameResolver;
  @Inject private Options options;

  /**
   * Returns the name of the given object. If the name will be used for an option and if the given object is a
   * <code>{@link Group}</code>, this method will return the name of the group in lower case.
   * @param e the given object.
   * @param usage indicates how the returned name will be used.
   * @return the name of the given object.
   */
  String nameOf(EObject e, NamingUsage usage) {
    if (NamingUsage.DEFAULT.equals(usage)) {
      return nameResolver.nameOf(e);
    }
    if (e instanceof IndexedElement) {
      return options.nameForOption((IndexedElement) e);
    }
    return nameResolver.nameOf(e);
  }

  /**
   * Indicates if the name to obtain will be used by a type (default) or an option.
   *
   * @author alruiz@google.com (Alex Ruiz)
   */
  static enum NamingUsage {
    DEFAULT, OPTION;
  }
}
