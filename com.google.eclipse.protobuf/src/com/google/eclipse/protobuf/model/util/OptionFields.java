/*
 * Copyright (c) 2011 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.model.util;

import com.google.eclipse.protobuf.protobuf.IndexedElement;
import com.google.eclipse.protobuf.protobuf.Option;
import com.google.eclipse.protobuf.protobuf.OptionField;
import com.google.inject.Singleton;

/**
 * Utility methods related to fields in <code>{@link Option}</code>s
 *
 * @author alruiz@google.com (Alex Ruiz)
 */
@Singleton public class OptionFields {
  /**
   * Returns the field the given option field is referring to.
   * @param field the given option field.
   * @return the field the given option field is referring to, or {@code null} if one cannot be found.
   */
  public IndexedElement sourceOf(OptionField field) {
    return (field == null) ? null : field.getTarget();
  }
}
