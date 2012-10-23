/*
 * Copyright (c) 2011 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.scoping;

import static java.util.Collections.emptyList;
import static java.util.Collections.unmodifiableList;

import static org.eclipse.xtext.resource.EObjectDescription.create;

import static com.google.common.collect.Lists.newArrayList;

import java.util.Collection;
import java.util.List;

import org.eclipse.xtext.resource.IEObjectDescription;

import com.google.eclipse.protobuf.protobuf.AbstractOption;
import com.google.eclipse.protobuf.protobuf.MessageField;
import com.google.inject.Inject;

/**
 * @author alruiz@google.com (Alex Ruiz)
 */
class NativeOptionDescriptions {
  @Inject private ProtoDescriptorProvider descriptorProvider;

  Collection<IEObjectDescription> sources(AbstractOption option) {
    ProtoDescriptor descriptor = descriptorProvider.primaryDescriptor();
    Collection<MessageField> optionSources = descriptor.availableOptionsFor(option.eContainer());
    if (optionSources.isEmpty()) {
      return emptyList();
    }
    return describe(optionSources);
  }

  private Collection<IEObjectDescription> describe(Collection<MessageField> fields) {
    List<IEObjectDescription> descriptions = newArrayList();
    for (MessageField field : fields) {
      String name = field.getName();
      descriptions.add(create(name, field));
    }
    return unmodifiableList(descriptions);
  }
}
