/*
 * Copyright (c) 2011 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.model.util;

import static org.eclipse.xtext.util.Strings.isEmpty;

import com.google.eclipse.protobuf.protobuf.*;
import com.google.inject.*;

import java.util.List;

/**
 * Utility methods related to <code>{@link Option}</code>.
 *
 * @author alruiz@google.com (Alex Ruiz)
 */
@Singleton
public class Options {

  private @Inject OptionFields optionFields;
  
  /**
   * Returns the <code>{@link IndexedElement}</code> the given <code>{@link Option}</code> is referring to. In the
   * following example
   * <pre>
   * option (myOption) = true;
   * </pre>
   * this method will return the <code>{@link IndexedElement}</code> "myOption" is pointing to.
   * @param option the given {@code Option}.
   * @return the {@code IndexedElement} the given {@code Option} is referring to, or {@code null} if it cannot be
   * found.
   */
  public IndexedElement sourceOf(Option option) {
    OptionSource source = option.getSource();
    return (source == null) ? null : source.getOptionField();
  }

  /**
   * Returns the last field of the given <code>{@link CustomOption}</code>.
   * In the following example
   * <pre>
   * option (myOption).foo = true;
   * </pre>
   * this method will return the field that "foo" is pointing to.
   * @param option the given {@code CustomOption}.
   * @return the last field of the given {@code CustomOption} is referring to, or {@code null} if one cannot be found.
   */
  public IndexedElement lastFieldSourceFrom(CustomOption option) {
    List<OptionFieldSource> fields = option.getOptionFields();
    if (fields.isEmpty()) return null;
    OptionFieldSource last = fields.get(fields.size() - 1);
    return optionFields.sourceOf(last);
  }
  
  /**
   * Returns the name of the given <code>{@link IndexedElement}</code>.
   * @param e the given {@code IndexedElement}.
   * @return the name of the given <code>{@link IndexedElement}</code>.
   */
  public String nameForOption(IndexedElement e) {
    if (e instanceof Property) return ((Property) e).getName();
    if (e instanceof Group) {
      String name = ((Group) e).getName();
      if (!isEmpty(name)) return name.toLowerCase();
    }
    return null;
  }
}
