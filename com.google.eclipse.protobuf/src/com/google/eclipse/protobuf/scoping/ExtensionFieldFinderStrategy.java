/*
 * Copyright (c) 2011, 2015 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.scoping;

import static java.util.Collections.emptyList;

import static org.eclipse.xtext.resource.EObjectDescription.create;

import static com.google.common.collect.Sets.newHashSet;

import java.util.Collection;
import java.util.Set;

import org.eclipse.xtext.resource.IEObjectDescription;

import com.google.eclipse.protobuf.model.util.MessageFields;
import com.google.eclipse.protobuf.model.util.Messages;
import com.google.eclipse.protobuf.model.util.Options;
import com.google.eclipse.protobuf.naming.OptionNamingStrategy;
import com.google.eclipse.protobuf.protobuf.IndexedElement;
import com.google.eclipse.protobuf.protobuf.Message;
import com.google.eclipse.protobuf.protobuf.MessageField;
import com.google.eclipse.protobuf.protobuf.TypeExtension;
import com.google.inject.Inject;

/**
 * @author alruiz@google.com (Alex Ruiz)
 */
class ExtensionFieldFinderStrategy implements CustomOptionFieldFinder.FinderStrategy {
  @Inject private MessageFields messageFields;
  @Inject private Messages messages;
  @Inject private OptionNamingStrategy namingStrategy;
  @Inject private Options options;
  @Inject private QualifiedNameDescriptions qualifiedNameDescriptions;

  @Override public Collection<IEObjectDescription> findOptionFields(IndexedElement reference) {
    if (!(reference instanceof MessageField)) {
      return emptyList();
    }
    Message fieldType = messageFields.messageTypeOf((MessageField) reference);
    if (fieldType == null) {
      return emptyList();
    }
    Set<IEObjectDescription> descriptions = newHashSet();
    for (TypeExtension extension : messages.localExtensionsOf(fieldType)) {
      for (IndexedElement element : extension.getElements()) {
        descriptions.addAll(qualifiedNameDescriptions.qualifiedNames(element, namingStrategy));
        String name = options.nameForOption(element);
        descriptions.add(create(name, element));
      }
    }
    return descriptions;
  }
}
