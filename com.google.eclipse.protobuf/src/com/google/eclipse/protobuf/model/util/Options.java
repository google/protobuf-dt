/*
 * Copyright (c) 2011 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.model.util;

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
   * Returns the <code>{@link Property}</code> the given <code>{@link Option}</code> is referring to. In the
   * following example
   * <pre>
   * option (myOption) = true;
   * </pre>
   * this method will return the <code>{@link Property}</code> "myOption" is pointing to.
   * @param option the given {@code Option}.
   * @return the {@code Property} the given {@code Option} is referring to, or {@code null} if it cannot be
   * found.
   */
  public Property propertyFrom(Option option) {
    PropertyRef ref = option.getProperty();
    return (ref == null) ? null : ref.getProperty();
  }

  /**
   * Returns the field of the <code>{@link Property}</code> the given <code>{@link CustomOption}</code> is referring to.
   * In the following example
   * <pre>
   * option (myOption).field = true;
   * </pre>
   * this method will return the <code>{@link Property}</code> "field" is pointing to.
   * @param option the given {@code Option}.
   * @return the field of the {@code Property} the given {@code CustomOption} is referring to, or {@code null} if one
   * cannot be found.
   */
  public Property fieldFrom(CustomOption option) {
    SimplePropertyRef ref = option.getPropertyField();
    return (ref == null) ? null : ref.getProperty();
  }
}
