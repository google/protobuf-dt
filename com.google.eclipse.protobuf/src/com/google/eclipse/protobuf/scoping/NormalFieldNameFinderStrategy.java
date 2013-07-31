/*
 * Copyright (c) 2012 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.scoping;

import static com.google.common.collect.Sets.newHashSet;
import static org.eclipse.xtext.resource.EObjectDescription.create;

import com.google.common.base.Strings;
import com.google.eclipse.protobuf.model.util.MessageFields;
import com.google.eclipse.protobuf.protobuf.Group;
import com.google.eclipse.protobuf.protobuf.IndexedElement;
import com.google.eclipse.protobuf.protobuf.MessageField;
import com.google.inject.Inject;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.xtext.resource.IEObjectDescription;

import java.util.Collection;
import java.util.Set;

/**
 * @author alruiz@google.com (Alex Ruiz)
 */
class NormalFieldNameFinderStrategy implements CustomOptionFieldNameFinder.FinderStrategy {
  @Inject private MessageFields messageFields;

  @Override public Collection<IEObjectDescription> findMessageFields(IndexedElement reference) {
    Set<IEObjectDescription> descriptions = newHashSet();
    Iterable<? extends EObject> elements = reference instanceof Group 
        ? ((Group) reference).getElements()
        : reference instanceof MessageField 
            ? messageFields.messageTypeOf((MessageField) reference).getElements()
            : null;

    if (elements != null) {
      for (EObject element : elements) {
        if (element instanceof MessageField) {
          String name = ((MessageField) element).getName();
          descriptions.add(create(name, element));
        } else if (element instanceof Group) {
          String name = ((Group) element).getName();
          if (!Strings.isNullOrEmpty(name)) {
            descriptions.add(create(name, element));
          }
        }
      }
    }
    return descriptions;
  }
}
