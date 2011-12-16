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

import java.util.List;

import com.google.eclipse.protobuf.protobuf.*;
import com.google.inject.*;

/**
 * Utility methods related to <code>{@link Option}</code>.
 *
 * @author alruiz@google.com (Alex Ruiz)
 */
@Singleton public class Options {

  private @Inject OptionFields optionFields;

  /**
   * Returns the <code>{@link IndexedElement}</code> the given <code>{@link CustomOption}</code> is referring to. This
   * method will check first the source of the last field of the given option (if any.) If the option does not have any
   * fields, this method will return the root source of the option.
   * <p>
   * Example #1
   *
   * <pre>
   * option(myOption) = true;
   * </pre>
   *
   * this method will return the <code>{@link IndexedElement}</code> "myOption" is pointing to.
   * </p>
   * <p>
   * Example #2
   *
   * <pre>
   * option(myOption).foo = true;
   * </pre>
   *
   * this method will return the <code>{@link IndexedElement}</code> "foo" is pointing to.
   * </p>
   * @param option the given {@code CustomOption}.
   * @return the {@code IndexedElement} the given {@code CustomOption} is referring to, or {@code null} if it cannot be
   * found.
   */
  public IndexedElement sourceOf(CustomOption option) {
    IndexedElement e = lastFieldSourceFrom(option);
    if (e == null) {
      e = rootSourceOf(option);
    }
    return e;
  }

  /**
   * Returns the <code>{@link IndexedElement}</code> the given <code>{@link Option}</code> is referring to. In the
   * following example
   *
   * <pre>
   * option(myOption).foo = true;
   * </pre>
   *
   * this method will return the <code>{@link IndexedElement}</code> "myOption" is pointing to.
   * @param option the given {@code Option}.
   * @return the {@code IndexedElement} the given {@code Option} is referring to, or {@code null} if it cannot be found.
   */
  public IndexedElement rootSourceOf(Option option) {
    OptionSource source = option.getSource();
    return (source == null) ? null : source.getTarget();
  }

  /**
   * Returns the last field of the given <code>{@link CustomOption}</code>. In the following example
   *
   * <pre>
   * option(myOption).foo = true;
   * </pre>
   *
   * this method will return the field that "foo" is pointing to.
   * @param option the given {@code CustomOption}.
   * @return the last field of the given {@code CustomOption} is referring to, or {@code null} if one cannot be found.
   */
  public IndexedElement lastFieldSourceFrom(CustomOption option) {
    List<OptionField> fields = option.getFields();
    if (fields.isEmpty()) {
      return null;
    }
    OptionField last = fields.get(fields.size() - 1);
    return optionFields.sourceOf(last);
  }

  /**
   * Returns the name of the given <code>{@link IndexedElement}</code> used as a source of an option. If the given
   * element is a <code>{@link Group}</code>, this method will return its name in lower case.
   * @param e the given {@code IndexedElement}.
   * @return the name of the given <code>{@link IndexedElement}</code>.
   */
  public String nameForOption(IndexedElement e) {
    if (e instanceof MessageField) {
      MessageField field = (MessageField) e;
      return field.getName();
    }
    if (e instanceof Group) {
      String name = ((Group) e).getName();
      // TODO check where toLowerCase is being used and remove duplication
      return (isEmpty(name)) ? name : name.toLowerCase();
    }
    return null;
  }
}
