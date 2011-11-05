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
 * Utility methods related to fields in <code>{@link Option}</code>s
 * 
 * @author alruiz@google.com (Alex Ruiz)
 */
@Singleton
public class OptionFields {

  /**
   * Returns the field the given option field is referring to.
   * @param fieldSource the given option field.
   * @return the field the given option field is referring to, or {@code null} if one cannot be found.
   */
  public Field sourceOf(OptionFieldSource fieldSource) {
    if (fieldSource instanceof OptionMessageFieldSource) {
      OptionMessageFieldSource source = (OptionMessageFieldSource) fieldSource;
      return source.getOptionMessageField();
    }
    if (fieldSource instanceof OptionExtendMessageFieldSource) {
      OptionExtendMessageFieldSource source = (OptionExtendMessageFieldSource) fieldSource;
      return source.getOptionExtendMessageField();
    }
    return null;
  }
}
