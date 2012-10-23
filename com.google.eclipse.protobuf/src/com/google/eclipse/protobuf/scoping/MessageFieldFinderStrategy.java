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

import java.util.Collection;
import java.util.Set;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.xtext.resource.IEObjectDescription;

import com.google.eclipse.protobuf.model.util.MessageFields;
import com.google.eclipse.protobuf.model.util.Options;
import com.google.eclipse.protobuf.protobuf.Group;
import com.google.eclipse.protobuf.protobuf.GroupElement;
import com.google.eclipse.protobuf.protobuf.IndexedElement;
import com.google.eclipse.protobuf.protobuf.Message;
import com.google.eclipse.protobuf.protobuf.MessageElement;
import com.google.eclipse.protobuf.protobuf.MessageField;
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
