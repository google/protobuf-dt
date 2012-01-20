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

import java.util.*;

import org.eclipse.xtext.resource.IEObjectDescription;

import com.google.eclipse.protobuf.model.util.MessageFields;
import com.google.eclipse.protobuf.protobuf.*;
import com.google.inject.Inject;

/**
 * @author alruiz@google.com (Alex Ruiz)
 */
class NormalFieldNameFinderStrategy implements CustomOptionFieldNameFinder.FinderStrategy {
  @Inject private MessageFields messageFields;

  @Override public Collection<IEObjectDescription> findMessageFields(MessageField reference) {
    Set<IEObjectDescription> descriptions = newHashSet();
    Message type = messageFields.messageTypeOf(reference);
    for (MessageElement element : type.getElements()) {
      if (element instanceof MessageField) {
        String name = ((MessageField) element).getName();
        descriptions.add(create(name, element));
      }
    }
    return descriptions;
  }
}
