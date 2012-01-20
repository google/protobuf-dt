/*
 * Copyright (c) 2011 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.scoping;

import static com.google.common.collect.Sets.newHashSet;
import static org.eclipse.xtext.resource.EObjectDescription.create;

import java.util.*;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.xtext.resource.IEObjectDescription;

import com.google.eclipse.protobuf.model.util.*;
import com.google.eclipse.protobuf.protobuf.*;
import com.google.inject.Inject;

/**
 * @author alruiz@google.com (Alex Ruiz)
 */
class MessageFieldFinderStrategy implements CustomOptionFieldFinder.FinderStrategy {
  @Inject private MessageFields messageFields;
  @Inject private Options options;

  @Override public Collection<IEObjectDescription> findOptionFields(IndexedElement reference) {
    Set<IEObjectDescription> descriptions = newHashSet();
    if (reference instanceof MessageField) {
      Message fieldType = messageFields.messageTypeOf((MessageField) reference);
      for (MessageElement element : fieldType.getElements()) {
        IEObjectDescription d = describe(element);
        if (d != null) {
          descriptions.add(d);
        }
      }
    }
    if (reference instanceof Group) {
      for (GroupElement element : ((Group) reference).getElements()) {
        IEObjectDescription d = describe(element);
        if (d != null) {
          descriptions.add(d);
        }
      }
    }
    return descriptions;
  }

  private IEObjectDescription describe(EObject e) {
    if (!(e instanceof IndexedElement)) {
      return null;
    }
    String name = options.nameForOption((IndexedElement) e);
    return create(name, e);
  }
}
