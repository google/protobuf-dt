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
    return option instanceof DefaultValueFieldOption && option.eContainer() instanceof Property;
  }

  /**
   * Returns the name of the given option.
   * @param option the given option.
   * @return the name of the given option.
   */
  public String nameOf(FieldOption option) {
    Property p = propertyFrom(option);
    return (p == null) ? null : p.getName();
  }
  
  /**
   * Returns the <code>{@link Property}</code> the given <code>{@link FieldOption}</code> is referring to. In the
   * following example
   * <pre>
   * [(myFieldOption) = true]
   * </pre>
   * this method will return the <code>{@link Property}</code> "myFieldOption" is pointing to.
   * @param option the given {@code FieldOption}.
   * @return the {@code Property} the given {@code FieldOption} is referring to, or {@code null} if it cannot be
   * found.
   */
  public Property propertyFrom(FieldOption option) {
    PropertyRef ref = null;
    if (option instanceof NativeFieldOption) {
      NativeFieldOption nativeOption = (NativeFieldOption) option;
      ref = nativeOption.getProperty();
    }
    if (option instanceof CustomFieldOption) {
      CustomFieldOption customOption = (CustomFieldOption) option;
      ref = customOption.getProperty();
    }
    return (ref == null) ? null : ref.getProperty();
  }

  /**
   * Returns the field of the <code>{@link Property}</code> the given <code>{@link CustomFieldOption}</code> is 
   * referring to. In the following example
   * <pre>
   * [(myFieldOption).field = true]
   * </pre>
   * this method will return the <code>{@link Property}</code> "field" is pointing to.
   * @param option the given {@code FieldOption}.
   * @return the field of the {@code Property} the given {@code CustomFieldOption} is referring to, or {@code null} if 
   * one cannot be found.
   */
  public Property fieldFrom(CustomFieldOption option) {
    return null;
    // SimplePropertyRef ref = option.getPropertyField();
    // return (ref == null) ? null : ref.getProperty();
  }
}
