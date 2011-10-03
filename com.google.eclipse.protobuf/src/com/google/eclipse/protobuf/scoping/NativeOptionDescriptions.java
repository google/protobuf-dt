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
  
  Collection <IEObjectDescription> properties(NativeOption option) {
    return allProperties(option);
  }

  Collection <IEObjectDescription> properties(NativeFieldOption option) {
    return allProperties(option);
  }

  private Collection <IEObjectDescription> allProperties(EObject option) {
    ProtoDescriptor descriptor = descriptorProvider.primaryDescriptor();
    Collection<Property> properties = descriptor.availableOptionsFor(option.eContainer());
    if (properties.isEmpty()) return emptyList();
    return describe(properties);
  }

  private Collection<IEObjectDescription> describe(Collection<Property> properties) {
    List<IEObjectDescription> descriptions = new ArrayList<IEObjectDescription>();
    for (Property p : properties) {
      descriptions.add(create(p.getName(), p));
    }
    return descriptions;
  }
}
