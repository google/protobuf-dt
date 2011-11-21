/*
 * Copyright (c) 2011 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.model.util;

import java.util.List;

import com.google.eclipse.protobuf.protobuf.*;
import com.google.inject.*;

/**
 * Utility methods related to field options.
 *
 * @author alruiz@google.com (Alex Ruiz)
 */
@Singleton
public class FieldOptions {

  private @Inject OptionFields optionFields;

  /**
   * Indicates whether the given option is the "default value" one.
   * @param option the given option to check.
   * @return {@code true} if the given option is the "default value" one, {@code false} otherwise.
   */
  public boolean isDefaultValueOption(FieldOption option) {
    return option instanceof DefaultValueFieldOption && option.eContainer() instanceof MessageField;
  }

  /**
   * Returns the name of the given option.
   * @param option the given option.
   * @return the name of the given option.
   */
  public String nameOf(FieldOption option) {
    IndexedElement e = rootSourceOf(option);
    if (e instanceof MessageField) {
      return ((MessageField) e).getName();
    }
    return null;
  }

  /**
   * Returns the <code>{@link IndexedElement}</code> the given <code>{@link CustomFieldOption}</code> is referring to. 
   * This method will check first the source of the last field of the given option (if any.) If the option does not have 
   * any fields, this method will return the root source of the option. 
   * <p>
   * Example #1
   * <pre>
   * [(myFieldOption) = true];
   * </pre>
   * this method will return the <code>{@link IndexedElement}</code> "myFieldOption" is pointing to.
   * </p>
   * <p>
   * Example #2
   * <pre>
   * [(myOption).foo = true];
   * </pre>
   * this method will return the <code>{@link IndexedElement}</code> "foo" is pointing to.
   * </p>
   * @param option the given {@code CustomFieldOption}.
   * @return the {@code IndexedElement} the given {@code CustomFieldOption} is referring to, or {@code null} if it 
   * cannot be found.
   */
  public IndexedElement sourceOf(CustomFieldOption option) {
    IndexedElement e = sourceOfLastFieldIn(option);
    if (e == null) e = rootSourceOf(option);
    return e;
  }
  
  /**
   * Returns the <code>{@link IndexedElement}</code> the given <code>{@link FieldOption}</code> is referring to. In the
   * following example
   * <pre>
   * [(myFieldOption) = true]
   * </pre>
   * this method will return the <code>{@link IndexedElement}</code> "myFieldOption" is pointing to.
   * @param option the given {@code FieldOption}.
   * @return the {@code Property} the given {@code FieldOption} is referring to, or {@code null} if it cannot be found.
   */
  public IndexedElement rootSourceOf(FieldOption option) {
    OptionSource source = null;
    if (option instanceof NativeFieldOption) {
      NativeFieldOption nativeOption = (NativeFieldOption) option;
      source = nativeOption.getSource();
    }
    if (option instanceof CustomFieldOption) {
      CustomFieldOption customOption = (CustomFieldOption) option;
      source = customOption.getSource();
    }
    return (source == null) ? null : source.getTarget();
  }

  /**
   * Returns the last field of the given <code>{@link CustomFieldOption}</code>.
   * In the following example
   * <pre>
   * [(myOption).foo = true];
   * </pre>
   * this method will return the field that "foo" is pointing to.
   * @param option the given {@code CustomFieldOption}.
   * @return the last field of the given {@code CustomFieldOption} is referring to, or {@code null} if one cannot be
   * found.
   */
  public IndexedElement sourceOfLastFieldIn(CustomFieldOption option) {
    List<OptionField> fields = option.getFields();
    if (fields.isEmpty()) return null;
    OptionField last = fields.get(fields.size() - 1);
    return optionFields.sourceOf(last);
  }
}
