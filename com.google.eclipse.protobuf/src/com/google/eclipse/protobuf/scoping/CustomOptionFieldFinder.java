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

import com.google.eclipse.protobuf.model.util.*;
import com.google.eclipse.protobuf.protobuf.*;
import com.google.inject.*;

import org.eclipse.xtext.resource.IEObjectDescription;

import java.util.Collection;

/**
 * @author alruiz@google.com (Alex Ruiz)
 */
class CustomOptionFieldFinder {
  @Inject private OptionFields optionFields;
  @Inject private Options options;

  Collection<IEObjectDescription> findOptionFields(AbstractCustomOption option,
      CustomOptionFieldFinderDelegate finder) {
    return findOptionFields(option, finder, null);
  }

  Collection<IEObjectDescription> findOptionFields(final AbstractCustomOption option,
      CustomOptionFieldFinderDelegate finder, OptionField field) {
    // TODO(alruiz): remove Provider of IndexedElement.
    IndexedElement e = referredField(option, field, new Provider<IndexedElement>() {
      @Override public IndexedElement get() {
        return options.rootSourceOf((AbstractOption) option);
      }
    });
    if (e != null) {
      return finder.findFieldsInType(e);
    }
    return emptySet();
  }

  private IndexedElement referredField(AbstractCustomOption option, OptionField field,
      Provider<IndexedElement> provider) {
    OptionField previous = null;
    boolean isFirstField = true;
    for (OptionField current : options.fieldsOf(option)) {
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
}
