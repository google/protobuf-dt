/*
 * Copyright (c) 2011 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.util;

import static com.google.eclipse.protobuf.grammar.CommonKeyword.DEFAULT;

import com.google.eclipse.protobuf.protobuf.*;
import com.google.inject.Singleton;

/**
 * Utility methods related to field options.
 *
 * @author alruiz@google.com (Alex Ruiz)
 */
@Singleton
public class FieldOptions {

  /**
   * Indicates whether the given option is the "default value" one.
   * @param option the given option to check.
   * @return {@code true} if the given option is the "default value" one, {@code false} otherwise.
   */
  public boolean isDefaultValueOption(FieldOption option) {
    return DEFAULT.hasValue(option.getName()) && option.eContainer() instanceof Property;
  }
}
