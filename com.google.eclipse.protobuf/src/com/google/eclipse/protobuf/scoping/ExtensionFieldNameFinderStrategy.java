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

import java.util.Collection;
import java.util.Set;

import org.eclipse.xtext.resource.IEObjectDescription;

import com.google.common.base.Strings;
import com.google.eclipse.protobuf.model.util.MessageFields;
import com.google.eclipse.protobuf.model.util.Messages;
import com.google.eclipse.protobuf.model.util.ModelObjects;
import com.google.eclipse.protobuf.naming.NormalNamingStrategy;
import com.google.eclipse.protobuf.protobuf.Group;
import com.google.eclipse.protobuf.protobuf.IndexedElement;
import com.google.eclipse.protobuf.protobuf.Message;
import com.google.eclipse.protobuf.protobuf.MessageElement;
import com.google.eclipse.protobuf.protobuf.MessageField;
import com.google.eclipse.protobuf.protobuf.TypeExtension;
import com.google.inject.Inject;

/**
 * @author alruiz@google.com (Alex Ruiz)
 */
class ExtensionFieldNameFinderStrategy implements CustomOptionFieldNameFinder.FinderStrategy {
  @Inject private MessageFields messageFields;
  @Inject private Messages messages;
  @Inject private ModelObjects modelObjects;
  @Inject private NormalNamingStrategy namingStrategy;
  @Inject private QualifiedNameDescriptions qualifiedNameDescriptions;

  @Override public Collection<IEObjectDescription> findMessageFields(IndexedElement reference) {
    Set<IEObjectDescription> descriptions = newHashSet();
    if (reference instanceof MessageField) {
      Message type = messageFields.messageTypeOf((MessageField) reference);
      // check first in descriptor.proto
      for (TypeExtension extension : messages.extensionsOf(type, modelObjects.rootOf(reference))) {
        for (MessageElement element : extension.getElements()) {
          if (element instanceof IndexedElement) {
            descriptions.addAll(qualifiedNameDescriptions.qualifiedNames(element, namingStrategy));
          }
        }
      }
    } else if (reference instanceof Group) {
      String name = ((Group) reference).getName();
      if (!Strings.isNullOrEmpty(name)) {
        descriptions.addAll(qualifiedNameDescriptions.qualifiedNames(reference, namingStrategy));
      }
    }
    return descriptions;
  }
}
