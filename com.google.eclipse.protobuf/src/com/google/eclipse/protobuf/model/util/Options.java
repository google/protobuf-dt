/*
 * Copyright (c) 2011 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.model.util;

import static java.util.Collections.unmodifiableList;

import static org.eclipse.xtext.util.Strings.isEmpty;

import java.util.List;

import com.google.eclipse.protobuf.naming.NameResolver;
import com.google.eclipse.protobuf.protobuf.AbstractCustomOption;
import com.google.eclipse.protobuf.protobuf.AbstractOption;
import com.google.eclipse.protobuf.protobuf.DefaultValueFieldOption;
import com.google.eclipse.protobuf.protobuf.FieldOption;
import com.google.eclipse.protobuf.protobuf.Group;
import com.google.eclipse.protobuf.protobuf.IndexedElement;
import com.google.eclipse.protobuf.protobuf.MessageField;
import com.google.eclipse.protobuf.protobuf.NativeFieldOption;
import com.google.eclipse.protobuf.protobuf.NativeOption;
import com.google.eclipse.protobuf.protobuf.Option;
import com.google.eclipse.protobuf.protobuf.OptionField;
import com.google.eclipse.protobuf.protobuf.OptionSource;
import com.google.inject.Inject;
import com.google.inject.Singleton;

/**
 * Utility methods related to <code>{@link Option}</code>s.
 *
 * @author alruiz@google.com (Alex Ruiz)
 */
@Singleton public class Options {
  @Inject private ModelObjects modelObjects;
  @Inject private NameResolver nameResolver;
  @Inject private OptionFields optionFields;

  public boolean isNative(AbstractOption option) {
    return option instanceof NativeOption || option instanceof NativeFieldOption;
  }

  @SuppressWarnings("unchecked")
  public List<OptionField> fieldsOf(AbstractCustomOption option) {
    List<OptionField> fields = modelObjects.valueOfFeature(option, "fields", List.class);
    return unmodifiableList(fields);
  }

  /**
   * Indicates whether the given option is the "default value" one.
   * @param option the given option to check.
   * @return {@code true} if the given option is the "default value" one, {@code false} otherwise.
   */
  public boolean isDefaultValueOption(FieldOption option) {
    return option instanceof DefaultValueFieldOption && option.eContainer() instanceof MessageField;
  }

  /**
   * Returns the <code>{@link IndexedElement}</code> the given custom option is referring to. This method will check
   * first the source of the last field of the given option (if any.) If the option does not have any fields, this
   * method will return the root source of the option.
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
   * @param option the given custom option.
   * @return the {@code IndexedElement} the given custom option is referring to, or {@code null} if it cannot be
   * found.
   */
  public IndexedElement sourceOf(AbstractCustomOption option) {
    IndexedElement e = sourceOfLastFieldIn(option);
    if (e == null) {
      e = rootSourceOf((AbstractOption) option);
    }
    return e;
  }

  /**
   * Returns the last field of the given custom option. In the following example
   *
   * <pre>
   * option(myOption).foo = true;
   * </pre>
   *
   * this method will return the field that "foo" is pointing to.
   * @param option the given custom option.
   * @return the last field of the given custom option is referring to, or {@code null} if one cannot be found.
   */
  @SuppressWarnings("unchecked")
  public IndexedElement sourceOfLastFieldIn(AbstractCustomOption option) {
    List<OptionField> fields = modelObjects.valueOfFeature(option, "fields", List.class);
    if (fields == null || fields.isEmpty()) {
      return null;
    }
    OptionField last = fields.get(fields.size() - 1);
    return optionFields.sourceOf(last);
  }

  /**
   * Returns the <code>{@link IndexedElement}</code> the given option is referring to. In the following example
   *
   * <pre>
   * option(myOption).foo = true;
   * </pre>
   *
   * this method will return the <code>{@link IndexedElement}</code> "myOption" is pointing to.
   * @param option the given option.
   * @return the {@code Property} the given option is referring to, or {@code null} if it cannot be found.
   */
  public IndexedElement rootSourceOf(AbstractOption option) {
    OptionSource source = modelObjects.valueOfFeature(option, "source", OptionSource.class);
    return source == null ? null : source.getTarget();
  }

  /**
   * Returns the name of the given <code>{@link IndexedElement}</code> that is being used as a source of an option. If
   * the given element is a <code>{@link Group}</code>, this method will return its name in lower case.
   * @param optionSource the given {@code IndexedElement} that is being used as a source of an option.
   * @return the name of the given {@code IndexedElement}.
   */
  public String nameForOption(IndexedElement optionSource) {
    String name = nameResolver.nameOf(optionSource);
    if (optionSource instanceof Group && !isEmpty(name)) {
      name = name.toLowerCase();
    }
    return name;
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
}
