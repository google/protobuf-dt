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

import org.eclipse.emf.ecore.EObject;

import com.google.eclipse.protobuf.naming.NameResolver;
import com.google.eclipse.protobuf.protobuf.*;
import com.google.inject.*;

/**
 * @author alruiz@google.com (Alex Ruiz)
 *
 */
@Singleton public class Options {

  @Inject private ModelObjects modelObjects;
  @Inject private NameResolver nameResolver;
  @Inject private OptionFields optionFields;

  /**
   * Indicates whether the given option is the "default value" one.
   * @param option the given option to check.
   * @return {@code true} if the given option is the "default value" one, {@code false} otherwise.
   */
  public boolean isDefaultValueOption(FieldOption option) {
    return option instanceof DefaultValueFieldOption && option.eContainer() instanceof MessageField;
  }

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
    IndexedElement e = sourceOfLastFieldIn(option);
    if (e == null) {
      e = rootSourceOf(option);
    }
    return e;
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
  public IndexedElement sourceOfLastFieldIn(CustomOption option) {
    return findSourceOfLastField(option);
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
    return findRootSource(option);
  }

  /**
   * Returns the <code>{@link IndexedElement}</code> the given <code>{@link CustomFieldOption}</code> is referring to.
   * This method will check first the source of the last field of the given option (if any.) If the option does not have
   * any fields, this method will return the root source of the option.
   * <p>
   * Example #1
   *
   * <pre>
   * [(myFieldOption) = true];
   * </pre>
   *
   * this method will return the <code>{@link IndexedElement}</code> "myFieldOption" is pointing to.
   * </p>
   * <p>
   * Example #2
   *
   * <pre>
   * [(myOption).foo = true];
   * </pre>
   *
   * this method will return the <code>{@link IndexedElement}</code> "foo" is pointing to.
   * </p>
   * @param option the given {@code CustomFieldOption}.
   * @return the {@code IndexedElement} the given {@code CustomFieldOption} is referring to, or {@code null} if it
   *         cannot be found.
   */
  public IndexedElement sourceOf(CustomFieldOption option) {
    IndexedElement e = sourceOfLastFieldIn(option);
    if (e == null) {
      e = rootSourceOf(option);
    }
    return e;
  }

  /**
   * Returns the last field of the given <code>{@link CustomFieldOption}</code>. In the following example
   *
   * <pre>
   * [(myOption).foo = true];
   * </pre>
   *
   * this method will return the field that "foo" is pointing to.
   * @param option the given {@code CustomFieldOption}.
   * @return the last field of the given {@code CustomFieldOption} is referring to, or {@code null} if one cannot be
   * found.
   */
  public IndexedElement sourceOfLastFieldIn(CustomFieldOption option) {
    return findSourceOfLastField(option);
  }

  @SuppressWarnings("unchecked")
  private IndexedElement findSourceOfLastField(EObject e) {
    List<OptionField> fields = modelObjects.valueOfFeature(e, "fields", List.class);
    if (fields == null || fields.isEmpty()) {
      return null;
    }
    OptionField last = fields.get(fields.size() - 1);
    return optionFields.sourceOf(last);
  }

  /**
   * Returns the <code>{@link IndexedElement}</code> the given <code>{@link FieldOption}</code> is referring to. In the
   * following example
   *
   * <pre>
   * [(myFieldOption) = true]
   * </pre>
   *
   * this method will return the <code>{@link IndexedElement}</code> "myFieldOption" is pointing to.
   * @param option the given {@code FieldOption}.
   * @return the {@code Property} the given {@code FieldOption} is referring to, or {@code null} if it cannot be found.
   */
  public IndexedElement rootSourceOf(FieldOption option) {
    return findRootSource(option);
  }

  private IndexedElement findRootSource(EObject e) {
    OptionSource source = modelObjects.valueOfFeature(e, "source", OptionSource.class);
    return source == null ? null : source.getTarget();
  }

  /**
   * Returns the name of the given <code>{@link IndexedElement}</code> used as a source of an option. If the given
   * element is a <code>{@link Group}</code>, this method will return its name in lower case.
   * @param e the given {@code IndexedElement}.
   * @return the name of the given <code>{@link IndexedElement}</code>.
   */
  public String nameForOption(IndexedElement e) {
    String name = nameResolver.nameOf(e);
    if (e instanceof Group && !isEmpty(name)) {
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
