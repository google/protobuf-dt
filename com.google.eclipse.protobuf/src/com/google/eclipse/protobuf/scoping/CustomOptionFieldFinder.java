/*
 * Copyright (c) 2011 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.scoping;

import static java.util.Collections.emptySet;

import java.util.Collection;

import org.eclipse.xtext.resource.IEObjectDescription;

import com.google.eclipse.protobuf.model.util.OptionFields;
import com.google.eclipse.protobuf.model.util.Options;
import com.google.eclipse.protobuf.protobuf.AbstractCustomOption;
import com.google.eclipse.protobuf.protobuf.AbstractOption;
import com.google.eclipse.protobuf.protobuf.IndexedElement;
import com.google.eclipse.protobuf.protobuf.OptionField;
import com.google.inject.Inject;
import com.google.inject.Provider;

/**
 * @author alruiz@google.com (Alex Ruiz)
 */
class CustomOptionFieldFinder {
  @Inject private OptionFields optionFields;
  @Inject private Options options;

  Collection<IEObjectDescription> findOptionFields(AbstractCustomOption customOption, FinderStrategy strategy) {
    return findOptionFields(customOption, strategy, null);
  }

  Collection<IEObjectDescription> findOptionFields(AbstractCustomOption customOption, FinderStrategy strategy,
      OptionField field) {
    // TODO(alruiz): remove Provider of IndexedElement.
    final AbstractOption option = (AbstractOption) customOption;
    IndexedElement e = referredField(customOption, field, new Provider<IndexedElement>() {
      @Override public IndexedElement get() {
        return options.rootSourceOf(option);
      }
    });
    if (e != null) {
      return strategy.findOptionFields(e);
    }
    return emptySet();
  }

  private IndexedElement referredField(AbstractCustomOption customOption, OptionField field,
      Provider<IndexedElement> provider) {
    OptionField previous = null;
    boolean isFirstField = true;
    for (OptionField current : options.fieldsOf(customOption)) {
      if (current == field) {
        return (isFirstField) ? provider.get() : optionFields.sourceOf(previous);
      }
      previous = current;
      isFirstField = false;
    }
    if (field == null) {
      if (previous == null) {
        return provider.get();
      }
      return optionFields.sourceOf(previous);
    }
    return null;
  }

  static interface FinderStrategy {
    Collection<IEObjectDescription> findOptionFields(IndexedElement reference);
  }
}
