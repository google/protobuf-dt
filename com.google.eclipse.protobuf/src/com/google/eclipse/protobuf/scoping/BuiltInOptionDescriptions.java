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

import org.eclipse.xtext.resource.IEObjectDescription;

import java.util.*;

/**
 * @author alruiz@google.com (Alex Ruiz)
 */
public class BuiltInOptionDescriptions {

  @Inject private ProtoDescriptorProvider descriptorProvider;

  Collection <IEObjectDescription> properties(BuiltInOption option) {
    ProtoDescriptor descriptor = descriptorProvider.primaryDescriptor();
    Collection<Property> optionProperties = descriptor.availableOptionPropertiesFor(option.eContainer());
    if (!optionProperties.isEmpty()) return describe(optionProperties);
    return emptyList();
  }

  private Collection<IEObjectDescription> describe(Collection<Property> optionProperties) {
    List<IEObjectDescription> descriptions = new ArrayList<IEObjectDescription>();
    for (Property p : optionProperties) {
      descriptions.add(create(p.getName(), p));
    }
    return descriptions;
  }
}
