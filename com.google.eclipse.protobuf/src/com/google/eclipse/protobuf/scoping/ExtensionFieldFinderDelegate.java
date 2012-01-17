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
import static java.util.Collections.emptyList;
import static org.eclipse.xtext.resource.EObjectDescription.create;

import com.google.eclipse.protobuf.model.util.*;
import com.google.eclipse.protobuf.protobuf.*;
import com.google.inject.Inject;

import org.eclipse.xtext.resource.IEObjectDescription;

import java.util.*;

/**
 * @author alruiz@google.com (Alex Ruiz)
 */
class ExtensionFieldFinderDelegate implements CustomOptionFieldFinderDelegate {
  @Inject private MessageFields messageFields;
  @Inject private Messages messages;
  @Inject private Options options;
  @Inject private QualifiedNameDescriptions qualifiedNameDescriptions;

  @Override public Collection<IEObjectDescription> findFieldsInType(IndexedElement e) {
    if (!(e instanceof MessageField)) {
      return emptyList();
    }
    Message fieldType = messageFields.messageTypeOf((MessageField) e);
    if (fieldType == null) {
      return emptyList();
    }
    Set<IEObjectDescription> descriptions = newHashSet();
    for (TypeExtension extension : messages.localExtensionsOf(fieldType)) {
      for (MessageElement element : extension.getElements()) {
        if (!(element instanceof IndexedElement)) {
          continue;
        }
        IndexedElement current = (IndexedElement) element;
        descriptions.addAll(qualifiedNameDescriptions.qualifiedNamesForOption(current));
        String name = options.nameForOption(current);
        descriptions.add(create(name, current));
      }
    }
    return descriptions;
  }
}
