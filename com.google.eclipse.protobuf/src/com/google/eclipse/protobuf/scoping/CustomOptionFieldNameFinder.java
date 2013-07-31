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

import com.google.eclipse.protobuf.model.util.Options;
import com.google.eclipse.protobuf.protobuf.AbstractCustomOption;
import com.google.eclipse.protobuf.protobuf.ComplexValue;
import com.google.eclipse.protobuf.protobuf.ComplexValueField;
import com.google.eclipse.protobuf.protobuf.FieldName;
import com.google.eclipse.protobuf.protobuf.IndexedElement;
import com.google.inject.Inject;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.xtext.resource.IEObjectDescription;

import java.util.Collection;

/**
 * @author alruiz@google.com (Alex Ruiz)
 */
class CustomOptionFieldNameFinder {
  @Inject private Options options;

  Collection<IEObjectDescription> findFieldNamesSources(ComplexValue value, FinderStrategy strategy) {
    IndexedElement source = sourceOf(value);
    if (source == null) {
      return emptySet();
    }
    return strategy.findMessageFields(source);
  }

  private IndexedElement sourceOf(ComplexValue value) {
    IndexedElement source = null;
    EObject container = value.eContainer();
    if (container instanceof AbstractCustomOption) {
      AbstractCustomOption option = (AbstractCustomOption) container;
      source = options.sourceOf(option);
    }
    if (container instanceof ComplexValueField) {
      source = sourceOfNameOf((ComplexValueField) container);
    }
    return source;
  }

  private IndexedElement sourceOfNameOf(ComplexValueField field) {
    FieldName name = field.getName();
    return (name == null) ? null : name.getTarget();
  }

  static interface FinderStrategy {
    Collection<IEObjectDescription> findMessageFields(IndexedElement reference);
  }
}
