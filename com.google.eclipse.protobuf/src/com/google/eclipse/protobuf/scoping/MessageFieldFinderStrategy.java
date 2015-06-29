/*
 * Copyright (c) 2011 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.scoping;

import static org.eclipse.xtext.resource.EObjectDescription.create;

import static com.google.common.collect.Sets.newHashSet;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Set;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.xtext.resource.IEObjectDescription;

import com.google.eclipse.protobuf.model.util.MessageFields;
import com.google.eclipse.protobuf.model.util.Options;
import com.google.eclipse.protobuf.protobuf.Group;
import com.google.eclipse.protobuf.protobuf.IndexedElement;
import com.google.eclipse.protobuf.protobuf.Message;
import com.google.eclipse.protobuf.protobuf.MessageField;
import com.google.eclipse.protobuf.protobuf.OneOf;
import com.google.inject.Inject;

/**
 * @author alruiz@google.com (Alex Ruiz)
 */
class MessageFieldFinderStrategy implements CustomOptionFieldFinder.FinderStrategy {
  @Inject private MessageFields messageFields;
  @Inject private Options options;

  @Override public Collection<IEObjectDescription> findOptionFields(IndexedElement reference) {
    Collection<? extends EObject> elements;
    if (reference instanceof MessageField) {
      Message fieldType = messageFields.mapEntryTypeOf((MessageField) reference);
      if (fieldType == null) {
        fieldType = messageFields.messageTypeOf((MessageField) reference);
      }

      if (fieldType != null) {
        elements = fieldType.getElements();
      } else {
        elements = Collections.emptySet();
      }
    } else if (reference instanceof Group) {
      elements = ((Group) reference).getElements();
    } else {
      elements = Collections.emptySet();
    }

    Set<IEObjectDescription> descriptions = newHashSet();
    Collection<EObject> expandedElements = expandOneOfs(elements);
    for (EObject element : expandedElements) {
      IEObjectDescription d = describe(element);
      if (d != null) {
        descriptions.add(d);
      }
    }
    return descriptions;
  }

  private Collection<EObject> expandOneOfs(Collection<? extends EObject> elements) {
    Collection<EObject> expandedElements = new ArrayList<>(elements.size());
    for (EObject element : elements) {
      if (element instanceof OneOf) {
        expandedElements.addAll(((OneOf) element).getElements());
      }
      else {
        expandedElements.add(element);
      }
    }
    return expandedElements;
  }

  private IEObjectDescription describe(EObject e) {
    if (!(e instanceof IndexedElement)) {
      return null;
    }
    String name = options.nameForOption((IndexedElement) e);
    return create(name, e);
  }
}
