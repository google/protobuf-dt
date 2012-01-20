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

import java.util.*;

import org.eclipse.xtext.resource.IEObjectDescription;

import com.google.eclipse.protobuf.model.util.*;
import com.google.eclipse.protobuf.protobuf.*;
import com.google.inject.Inject;

/**
 * @author alruiz@google.com (Alex Ruiz)
 */
class ExtensionFieldNameFinderStrategy implements CustomOptionFieldNameFinder.FinderStrategy {
  @Inject private MessageFields messageFields;
  @Inject private Messages messages;
  @Inject private ModelObjects modelObjects;
  @Inject private QualifiedNameDescriptions qualifiedNameDescriptions;

  @Override public Collection<IEObjectDescription> findMessageFields(MessageField reference) {
    Set<IEObjectDescription> descriptions = newHashSet();
    Message type = messageFields.messageTypeOf(reference);
    // check first in descriptor.proto
    for (TypeExtension extension : messages.extensionsOf(type, modelObjects.rootOf(reference))) {
      for (MessageElement element : extension.getElements()) {
        if (!(element instanceof MessageField)) {
          continue;
        }
        descriptions.addAll(qualifiedNameDescriptions.qualifiedNames(element));
      }
    }
    return descriptions;
  }
}
