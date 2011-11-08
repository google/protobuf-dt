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
   * Returns the <code>{@link Field}</code> the given <code>{@link Option}</code> is referring to. In the
   * following example
   * <pre>
   * option (myOption) = true;
   * </pre>
   * this method will return the <code>{@link Field}</code> "myOption" is pointing to.
   * @param option the given {@code Option}.
   * @return the {@code Field} the given {@code Option} is referring to, or {@code null} if it cannot be
   * found.
   */
  public Field sourceOf(Option option) {
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
  public Field lastFieldSourceFrom(CustomOption option) {
    List<OptionFieldSource> fields = option.getOptionFields();
    if (fields.isEmpty()) return null;
    OptionFieldSource last = fields.get(fields.size() - 1);
    return optionFields.sourceOf(last);
  }
  
  public String nameForOption(Field f) {
    String name = f.getName();
    if (isEmpty(name)) return name;
    if (f instanceof Group) name = name.toLowerCase();
    return name;
  }
}
