/*
 * Copyright (c) 2011 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.util;

import org.eclipse.emf.ecore.EObject;

import com.google.eclipse.protobuf.protobuf.*;
import com.google.inject.Singleton;

/**
 * Utility methods related to <code>{@link Option}</code>.
 *
 * @author alruiz@google.com (Alex Ruiz)
 */
@Singleton
public class Options {

  /**
   * Indicates whether the given option is a file-level option.
   * @param option the option to verify.
   * @return {@code true} if the given option is a file-level option, {@code false} otherwise.
   */
  public boolean isFileOption(Option option) {
    return isOptionContainerInstanceOf(option, Protobuf.class);
  }

  private boolean isOptionContainerInstanceOf(Option option, Class<? extends EObject> type) {
    return type.isInstance(option.eContainer());
  }

  /**
   * Returns the <code>{@link Property}</code> the given <code>{@link Option}</code> is referring to.
   * @param option the given {@code BuiltInFileOption}.
   * @return the {@code Property} the given {code BuiltInFileOption} is referring to, or {@code null} if it cannot be
   * found.
   */
  public Property propertyFrom(Option option) {
    PropertyRef ref = option.getProperty();
    return (ref == null) ? null : ref.getProperty();
  }
}
