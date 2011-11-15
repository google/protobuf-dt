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
import static org.eclipse.xtext.resource.EObjectDescription.create;

import com.google.eclipse.protobuf.protobuf.*;
import com.google.inject.Inject;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.xtext.resource.IEObjectDescription;

import java.util.*;

/**
 * @author alruiz@google.com (Alex Ruiz)
 */
class NativeOptionDescriptions {

  @Inject private ProtoDescriptorProvider descriptorProvider;
  
  Collection <IEObjectDescription> sources(NativeOption option) {
    return allSources(option);
  }

  Collection <IEObjectDescription> sources(NativeFieldOption option) {
    return allSources(option);
  }

  private Collection <IEObjectDescription> allSources(EObject option) {
    ProtoDescriptor descriptor = descriptorProvider.primaryDescriptor();
    Collection<MessageField> optionSources = descriptor.availableOptionsFor(option.eContainer());
    if (optionSources.isEmpty()) return emptyList();
    return describe(optionSources);
  }

  private Collection<IEObjectDescription> describe(Collection<MessageField> fields) {
    List<IEObjectDescription> descriptions = new ArrayList<IEObjectDescription>();
    for (MessageField field : fields) {
      descriptions.add(create(field.getName(), field));
    }
    return descriptions;
  }
}
